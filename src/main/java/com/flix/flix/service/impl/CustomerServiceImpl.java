package com.flix.flix.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EGender;
import com.flix.flix.constant.custom_enum.EGenre;
import com.flix.flix.entity.AppUser;
import com.flix.flix.entity.Customer;
import com.flix.flix.entity.FavGenre;
import com.flix.flix.entity.Product;
import com.flix.flix.model.request.NewCustomerRequest;
import com.flix.flix.model.response.CustomerResponse;
import com.flix.flix.repository.CustomerRepository;
import com.flix.flix.repository.FavGenreRepository;
import com.flix.flix.repository.ProductRepository;
import com.flix.flix.service.CustomerService;
import com.flix.flix.service.FavGenreService;
import com.flix.flix.service.ProductService;
import com.flix.flix.util.DateUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final FavGenreService favGenreService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final FavGenreRepository favGenreRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CustomerResponse create(AppUser appUser, NewCustomerRequest newCustomerRequest) {
        try {
            Customer customer = Customer.builder()
                    .fullname(newCustomerRequest.getFullname())
                    .country(newCustomerRequest.getCountry())
                    .city(newCustomerRequest.getCity())
                    .phoneNumber(newCustomerRequest.getPhoneNumber())
                    .gender(EGender.findByDescription(newCustomerRequest.getGender()))
                    .registrationDate(LocalDate.now())
                    .lastLogin(LocalDate.now())
                    .appUser(appUser)
                    .build();
            return toCustomerResponse(customerRepository.saveAndFlush(customer));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<CustomerResponse> getAll() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerResponse> customerResponses = customers.stream().map(customer -> toCustomerResponse(customer)).toList();
        return customerResponses;
    }

    @Override
    public CustomerResponse getById(String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) throw new RuntimeException(DbBash.CUSTOMER_NOT_FOUND);
        Customer customer_ = customer.get();

        return toCustomerResponse(customer_);
    }

    @Override
    public Customer getCustomerById(String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) throw new RuntimeException(DbBash.CUSTOMER_NOT_FOUND);
        return customer.get();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CustomerResponse update(String id, NewCustomerRequest newCustomerRequest) {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException(DbBash.CUSTOMER_NOT_FOUND));

            customer.setFullname(newCustomerRequest.getFullname());
            customer.setCountry(newCustomerRequest.getCountry());
            customer.setPhoneNumber(newCustomerRequest.getPhoneNumber());
            customer.setCity(newCustomerRequest.getCity());
            customer.setGender(EGender.findByDescription(newCustomerRequest.getGender()));
            customer.setRegistrationDate(DateUtil.parseDate(newCustomerRequest.getRegistrationDate()));
            customer.setLastLogin(DateUtil.parseDate(newCustomerRequest.getLastLogin()));

            List<FavGenre> favGenres = customer.getFavGenre();
            if (favGenres == null) {
                favGenres = new ArrayList<>();
            }
            List<FavGenre> toRemove = new ArrayList<>(favGenres);

            if (newCustomerRequest.getFavGenre() != null) {
                for (String favGenreString : newCustomerRequest.getFavGenre()) {
                    Optional<FavGenre> existingFavGenre = favGenres.stream()
                        .filter(fg -> fg.getFavGenre().equals(EGenre.findByDescription(favGenreString)))
                        .findFirst();

                    if (existingFavGenre.isEmpty()) {
                        FavGenre favGenre = favGenreService.create(
                            FavGenre.builder()
                                .customer(customer)
                                .favGenre(EGenre.findByDescription(favGenreString))
                                .build()
                        );
                        favGenres.add(favGenre);
                    } else {
                        toRemove.remove(existingFavGenre.get());
                    }
                }
            }

            favGenres.removeAll(toRemove);
            // favGenreRepository.deleteAll(toRemove);

            if (newCustomerRequest.getLikeProductId() != null && !newCustomerRequest.getLikeProductId().isEmpty()) {
                for (String productId : newCustomerRequest.getLikeProductId()) {
                    Product product = productService.getProductById(productId);

                    if (!product.containsCustomerLike(customer)) {
                        if (product.containsCustomerDislike(customer)) {
                            product.getCustomerDislike().remove(customer);
                            customer.getDislikeProduct().remove(product);
                        }
                        product.getCustomerLike().add(customer);
                        customer.getLikeProduct().add(product);
                        productRepository.save(product);
                    }
                }
            }

            if (newCustomerRequest.getDislikeProductId() != null && !newCustomerRequest.getDislikeProductId().isEmpty()) {
                for (String productId : newCustomerRequest.getDislikeProductId()) {
                    Product product = productService.getProductById(productId);

                    if (!product.containsCustomerDislike(customer)) {
                        if (product.containsCustomerLike(customer)) {
                            product.getCustomerLike().remove(customer);
                            customer.getLikeProduct().remove(product);
                        }
                        product.getCustomerDislike().add(customer);
                        customer.getDislikeProduct().add(product);
                        productRepository.save(product);
                    }
                }
            }
            
            customerRepository.saveAndFlush(customer);

            return toCustomerResponse(customer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) throw new RuntimeException(DbBash.CUSTOMER_NOT_FOUND);
        Customer customer_ = customer.get();
        customerRepository.delete(customer_);
    }

    
    private CustomerResponse toCustomerResponse(Customer customer) {

        if (customer.getFavGenre() == null) {
            return CustomerResponse.builder()
                    .id(customer.getId())   
                    .fullname(customer.getFullname())
                    .country(customer.getCountry()) 
                    .phoneNumber(customer.getPhoneNumber())
                    .city(customer.getCity())  
                    .gender(customer.getGender().toString())
                    .registrationDate(customer.getRegistrationDate().toString())
                    .lastLogin(customer.getLastLogin().toString())
                    .build();
        }
        return CustomerResponse.builder()
                .id(customer.getId())  
                .fullname(customer.getFullname())
                .country(customer.getCountry())
                .phoneNumber(customer.getPhoneNumber())
                .city(customer.getCity())  
                .gender(customer.getGender().toString())
                .registrationDate(customer.getRegistrationDate().toString())
                .lastLogin(customer.getLastLogin().toString())
                .favGenre(customer.getFavGenre().stream().map(favGenre -> favGenre.getFavGenre().getDescription()).toList())
                .likeProductId(customer.getLikeProduct().stream().map(product -> product.getId()).toList())
                .dislikeProductId(customer.getDislikeProduct().stream().map(product -> product.getId()).toList())
                .build();
    }
}
