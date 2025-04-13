package com.flix.flix.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.customEnum.EPaymentMethod;
import com.flix.flix.constant.customEnum.ESeat;
import com.flix.flix.entity.Customer;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductPricing;
import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.entity.Studio;
import com.flix.flix.entity.Theater;
import com.flix.flix.entity.Transaction;
import com.flix.flix.model.request.TransactionRequest;
import com.flix.flix.model.response.TransactionResponse;
import com.flix.flix.repository.TransactionRepository;
import com.flix.flix.service.CustomerService;
import com.flix.flix.service.ProductPricingService;
import com.flix.flix.service.ProductSchedulingService;
import com.flix.flix.service.ProductService;
import com.flix.flix.service.StudioService;
import com.flix.flix.service.TheaterService;
import com.flix.flix.service.TransactionService;
import com.flix.flix.util.DateUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;
    private final TheaterService theaterService;
    private final StudioService studioService;
    private final ProductService productService;
    private final ProductPricingService productPricingService;
    private final ProductSchedulingService productSchedulingService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionResponse create(TransactionRequest transactionRequest) {
        try {
            Customer customer = customerService.getCustomerById(transactionRequest.getCustomerId());
            Theater theater = theaterService.getTheaterById(transactionRequest.getTheaterId());
            Studio studio = studioService.getStudioById(transactionRequest.getStudioId());
            Product product = productService.getProductById(transactionRequest.getProductId());
            ProductPricing productPricing = productPricingService.getProductPricingById(transactionRequest.getProductPricingId());
            ProductScheduling productScheduling = productSchedulingService.getProductSchedulingById(transactionRequest.getProductSchedulingId());

            if (studio.getTheater().getId() != theater.getId()) throw new RuntimeException(DbBash.THEATER_AND_STUDIO_NOT_MATCH);
            for (String seat : transactionRequest.getSeats()) {
                if (!studio.getAvailableSeat().contains(ESeat.findByDescription(seat))) throw new RuntimeException(DbBash.STUDIO_SEAT_NOT_MATCH);
            }
            if (studio.getProductPricingScheduling().stream()
                    .noneMatch(productPricingScheduling ->
                            productPricingScheduling.getProductPricing().getId().equals(productPricing.getId()))) {
                throw new RuntimeException(DbBash.PRODUCT_PRICING_NOT_MATCH);
            }
            if (studio.getProductPricingScheduling().stream()
                    .noneMatch(productPricingScheduling ->
                            productPricingScheduling.getProductScheduling().stream()
                                    .anyMatch(productScheduling1 ->
                                            productScheduling1.getId().equals(productScheduling.getId())))) {
                throw new RuntimeException(DbBash.PRODUCT_SCHEDULING_NOT_MATCH);
            }

            Transaction transaction = Transaction.builder()
                    .customer(customer)
                    .theater(theater)
                    .studio(studio)
                    .product(product)
                    .productPricing(productPricing)
                    .productScheduling(productScheduling)
                    .qty(transactionRequest.getQty())
                    .tax(transactionRequest.getTax())
                    .transactionDate(DateUtil.parseDate(transactionRequest.getTransactionDate()))
                    .paymentStatus(transactionRequest.getPaymentStatus())
                    .paymentDateTime(DateUtil.parseDateTime(transactionRequest.getPaymentDateTime()))
                    .paymentMethod(EPaymentMethod.findByDescription(transactionRequest.getPaymentMethod()))
                    .seats(ESeat.toESeatList(transactionRequest.getSeats()))
                    .createdAt(LocalDate.now())
                    .updatedAt(LocalDate.now())
                    .build();
            return toTransactionResponse(transactionRepository.save(transaction));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TransactionResponse> getAll() {
        try {
            return transactionRepository.findAll().stream().map(transaction -> toTransactionResponse(transaction)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Transaction getTransactionById(String id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isEmpty()) throw new RuntimeException(DbBash.TRANSACTION_NOT_FOUND);
        return transaction.get();
    }

    @Override
    public TransactionResponse getById(String id) {
        try {
            return toTransactionResponse(getTransactionById(id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionResponse update(TransactionRequest transactionRequest, String id) {
        try {
            Transaction transaction = getTransactionById(id);
            transaction.setQty(transactionRequest.getQty());
            transaction.setTax(transactionRequest.getTax());
            transaction.setPaymentStatus(transactionRequest.getPaymentStatus());
            transaction.setPaymentDateTime(DateUtil.parseDateTime(transactionRequest.getPaymentDateTime()));
            transaction.setPaymentMethod(EPaymentMethod.findByDescription(transactionRequest.getPaymentMethod()));
            transaction.setSeats(ESeat.toESeatList(transactionRequest.getSeats()));
            return toTransactionResponse(transactionRepository.save(transaction));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(transaction.getId())
                .customerId(transaction.getCustomer().getId())
                .theaterId(transaction.getTheater().getId())
                .studioId(transaction.getStudio().getId())
                .productId(transaction.getProduct().getId())
                .productPricingId(transaction.getProductPricing().getId())
                .productSchedulingId(transaction.getProductScheduling().getId())
                .qty(transaction.getQty())
                .tax(transaction.getTax())
                .transactionDate(transaction.getTransactionDate().toString())
                .paymentStatus(transaction.getPaymentStatus())
                .paymentDateTime(transaction.getPaymentDateTime().toString())
                .paymentMethod(transaction.getPaymentMethod().getDescription())
                .seats(transaction.getSeats().stream().map(ESeat::getDescription).toList())
                .createdAt(transaction.getCreatedAt().toString())
                .updatedAt(transaction.getUpdatedAt().toString())
                .build();
        return transactionResponse;
    }
}
