package com.flix.flix.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EGender;
import com.flix.flix.constant.custom_enum.EGenre;
import com.flix.flix.constant.custom_enum.ERole;
import com.flix.flix.entity.AppUser;
import com.flix.flix.entity.Customer;
import com.flix.flix.entity.FavGenre;
import com.flix.flix.entity.Product;
import com.flix.flix.model.request.NewCustomerRequest;
import com.flix.flix.model.request.NewUserRequest;
import com.flix.flix.model.request.UpdateCustomerRequest;
import com.flix.flix.model.request.search.SearchCustomerRequest;
import com.flix.flix.model.response.CustomerResponse;
import com.flix.flix.model.response.SignupResponse;
import com.flix.flix.repository.CustomerRepository;
import com.flix.flix.repository.ProductRepository;
import com.flix.flix.service.AppUserService;
import com.flix.flix.service.CustomerService;
import com.flix.flix.service.FavGenreService;
import com.flix.flix.service.ProductService;
import com.flix.flix.specification.CustomerSpecification;
import com.flix.flix.util.DateUtil;
import com.flix.flix.util.TokenUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final FavGenreService favGenreService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final AppUserService appUserService;
    private final TokenUtil tokenUtil;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CustomerResponse create(NewCustomerRequest newCustomerRequest) {
        try {
            NewUserRequest userRequest = NewUserRequest.builder()
                    .email(newCustomerRequest.getEmail())
                    .password(newCustomerRequest.getPassword())
                    .username(newCustomerRequest.getUsername())
                    .role(newCustomerRequest.getRole())
                    .build();
            SignupResponse signupResponse = appUserService.signup(userRequest);
            AppUser appUser = appUserService.getAppUserById(signupResponse.getAccountId());

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
            appUser.setCustomer(customer);
            return toCustomerResponse(customerRepository.saveAndFlush(customer));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<CustomerResponse> getAll(SearchCustomerRequest searchCustomerRequest) {
        try {
            if (searchCustomerRequest.getPage() <= 0 || searchCustomerRequest.getSize() <= 0) {
                searchCustomerRequest.setPage(1);
                searchCustomerRequest.setSize(10);
            }
            Sort sort = Sort.by(Sort.Direction.fromString(searchCustomerRequest.getDirection()), searchCustomerRequest.getSortBy());
            Pageable pageable = PageRequest.of(searchCustomerRequest.getPage() - 1, searchCustomerRequest.getSize(), sort);
            Specification<Customer> specification = CustomerSpecification.getSpecification(searchCustomerRequest);
            return customerRepository.findAll(specification, pageable).map(this::toCustomerResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CustomerResponse getById(String id) {
        try {
            Customer customer = getCustomerById(id);
            return toCustomerResponse(customer);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Customer getCustomerById(String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) throw new RuntimeException(DbBash.CUSTOMER_NOT_FOUND);
        return customer.get();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CustomerResponse update(String id, UpdateCustomerRequest updateCustomerRequest) {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException(DbBash.CUSTOMER_NOT_FOUND));

            customer.setFullname(updateCustomerRequest.getFullname());
            customer.setCountry(updateCustomerRequest.getCountry());
            customer.setPhoneNumber(updateCustomerRequest.getPhoneNumber());
            customer.setCity(updateCustomerRequest.getCity());
            customer.setGender(EGender.findByDescription(updateCustomerRequest.getGender()));
            customer.setRegistrationDate(DateUtil.parseDate(updateCustomerRequest.getRegistrationDate()));
            customer.setLastLogin(DateUtil.parseDate(updateCustomerRequest.getLastLogin()));

            List<FavGenre> favGenres = customer.getFavGenre();
            if (favGenres == null) {
                favGenres = new ArrayList<>();
            }
            List<FavGenre> toRemove = new ArrayList<>(favGenres);

            if (updateCustomerRequest.getFavGenre() != null) {
                for (String favGenreString : updateCustomerRequest.getFavGenre()) {
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

            if (updateCustomerRequest.getLikeProductId() != null && !updateCustomerRequest.getLikeProductId().isEmpty()) {
                for (String productId : updateCustomerRequest.getLikeProductId()) {
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

            if (updateCustomerRequest.getDislikeProductId() != null && !updateCustomerRequest.getDislikeProductId().isEmpty()) {
                for (String productId : updateCustomerRequest.getDislikeProductId()) {
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
        try {
            return CustomerResponse.builder()
                    .id(customer.getId())
                    .userId(customer.getAppUser().getId())
                    .fullname(customer.getFullname())
                    .country(customer.getCountry())
                    .phoneNumber(customer.getPhoneNumber())
                    .city(customer.getCity())  
                    .gender(customer.getGender().toString())
                    .registrationDate(customer.getRegistrationDate().toString())
                    .lastLogin(customer.getLastLogin().toString())
                    .favGenre(customer.getFavGenre() == null ? null : customer.getFavGenre().stream().map(fg -> fg.getFavGenre().toString()).toList())
                    .likeProductId(customer.getLikeProduct().stream().map(product -> product.getId()).toList())
                    .dislikeProductId(customer.getDislikeProduct().stream().map(product -> product.getId()).toList())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CustomerResponse updateByCredentials(HttpServletRequest httpServletRequest, UpdateCustomerRequest UpdateCustomerRequest) {
        try {
            AppUser userAccount = tokenUtil.getAppUserByToken(httpServletRequest);
            System.out.println("ASDFKASJHJASDJK" + userAccount.getEmail());
            System.out.println("ASDFKASJHJASDJK" + userAccount.getRole());

            if (!userAccount.getRole().contains(ERole.ROLE_CUSTOMER)) throw new RuntimeException(DbBash.UNAUTHORIZED);

            return update(userAccount.getCustomer().getId(), UpdateCustomerRequest);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
