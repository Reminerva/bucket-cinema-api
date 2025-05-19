package com.flix.flix.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EPaymentMethod;
import com.flix.flix.constant.custom_enum.EPaymentStatus;
import com.flix.flix.constant.custom_enum.ERole;
import com.flix.flix.constant.custom_enum.ESeat;
import com.flix.flix.constant.custom_enum.ETax;
import com.flix.flix.entity.AppUser;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductPricing;
import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.entity.Studio;
import com.flix.flix.entity.StudioSeatSchedule;
import com.flix.flix.entity.Theater;
import com.flix.flix.entity.Transaction;
import com.flix.flix.model.request.NewStudioSeatScheduleRequest;
import com.flix.flix.model.request.NewTransactionRequest;
import com.flix.flix.model.response.TransactionResponse;
import com.flix.flix.repository.TransactionRepository;
import com.flix.flix.service.CustomerService;
import com.flix.flix.service.EmployeeService;
import com.flix.flix.service.ProductPricingService;
import com.flix.flix.service.ProductSchedulingService;
import com.flix.flix.service.ProductService;
import com.flix.flix.service.StudioSeatScheduleService;
import com.flix.flix.service.StudioService;
import com.flix.flix.service.TheaterService;
import com.flix.flix.service.TransactionService;
import com.flix.flix.util.DateUtil;
import com.flix.flix.util.TokenUtil;

import jakarta.servlet.http.HttpServletRequest;
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
    private final EmployeeService employeeService;
    private final StudioSeatScheduleService studioSeatScheduleService;
    private final TokenUtil tokenUtil;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionResponse create(NewTransactionRequest transactionRequest, HttpServletRequest request) {
        try {

            Theater theater = theaterService.getTheaterById(transactionRequest.getTheaterId());
            Studio studio = studioService.getStudioById(transactionRequest.getStudioId());
            Product product = productService.getProductById(transactionRequest.getProductId());
            ProductPricing productPricing = productPricingService.getProductPricingById(transactionRequest.getProductPricingId());
            ProductScheduling productScheduling = productSchedulingService.getProductSchedulingById(transactionRequest.getProductSchedulingId());

            validateRequest(transactionRequest, theater, studio, product, productPricing, productScheduling, transactionRequest.getQty(), transactionRequest.getSeats());

            Transaction transaction = Transaction.builder()
                    .customer(null)
                    .employee(null)
                    .theater(theater)
                    .studio(studio)
                    .product(product)
                    .watchDate(DateUtil.parseDate(transactionRequest.getWatchDate()))
                    .productPricing(productPricing)
                    .productScheduling(productScheduling)
                    .qty(transactionRequest.getQty())
                    .tax(ETax.findByValue(transactionRequest.getTax()))
                    .transactionDateTime(DateUtil.parseDateTime(transactionRequest.getTransactionDateTime()))
                    .paymentStatus(EPaymentStatus.PAYMENT_STATUS_PENDING)
                    .paymentDateTime(DateUtil.parseDateTime(transactionRequest.getPaymentDateTime()))
                    .paymentMethod(EPaymentMethod.findByDescription(transactionRequest.getPaymentMethod()))
                    .seats(ESeat.toESeatList(transactionRequest.getSeats()))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .expirationDate(DateUtil.parseDateTime(transactionRequest.getTransactionDateTime()).plusHours(1))
                    .build();
            
            // update studio seat
            updateStudioSeatSchedule(studio, productScheduling, transaction);

            System.out.println("NIININININ1: ");
            AppUser appUser = tokenUtil.getAppUserByToken(request);
            System.out.println("NIININININ2: " + appUser.getRole().toString());
            if (appUser.getRole().contains(ERole.ROLE_CUSTOMER)) {
                transaction.setCustomer(customerService.getCustomerById(appUser.getCustomer().getId()));
                System.out.println("NIININININ3: " + appUser.getEmail());
                return toTransactionResponse(transactionRepository.saveAndFlush(transaction));
            } else if (appUser.getRole().contains(ERole.ROLE_CASHIER)) {
                transaction.setEmployee(employeeService.getEmployeeById(appUser.getCustomer().getId()));
                System.out.println("NIININININ4: " + appUser.getEmail());
                return toTransactionResponse(transactionRepository.saveAndFlush(transaction));
            } else {
                System.out.println("NIININININ5: " + appUser.getEmail());
                throw new RuntimeException(DbBash.ONLY_CASHIER_OR_CUSTOMER_CAN_CREATE_TRANSACTION);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<TransactionResponse> getAll() {
        try {
            return transactionRepository.findAll().stream().map(transaction -> toTransactionResponse(transaction)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
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
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionResponse update(NewTransactionRequest transactionRequest, String id, HttpServletRequest request) {
        try {
            Studio studio = studioService.getStudioById(transactionRequest.getStudioId());
            Theater theater = theaterService.getTheaterById(transactionRequest.getTheaterId());
            Product product = productService.getProductById(transactionRequest.getProductId());
            ProductPricing productPricing = productPricingService.getProductPricingById(transactionRequest.getProductPricingId());
            ProductScheduling productScheduling = productSchedulingService.getProductSchedulingById(transactionRequest.getProductSchedulingId());

            validateRequest(transactionRequest, theater, studio, product, productPricing, productScheduling, transactionRequest.getQty(), transactionRequest.getSeats());

            Transaction transaction = getTransactionById(id);

            updateStudioSeatSchedule(studio, productScheduling, transaction);

            AppUser appUser = tokenUtil.getAppUserByToken(request);
            if (!appUser.getRole().contains(ERole.ROLE_ADMIN)) {
                throw new RuntimeException(DbBash.ONLY_ADMIN_CAN_UPDATE_TRANSACTION);
            }

            transaction.setQty(transactionRequest.getQty());
            transaction.setTax(ETax.findByValue(transactionRequest.getTax()));
            transaction.setTransactionDateTime(DateUtil.parseDateTime(transactionRequest.getTransactionDateTime()));
            transaction.setWatchDate(DateUtil.parseDate(transactionRequest.getWatchDate()));
            transaction.setProductPricing(productPricing);
            transaction.setProductScheduling(productScheduling);
            transaction.setStudio(studio);
            transaction.setTheater(theater);
            transaction.setProduct(product);
            transaction.setPaymentStatus(EPaymentStatus.findByDescription(transactionRequest.getPaymentStatus()));
            transaction.setPaymentDateTime(DateUtil.parseDateTime(transactionRequest.getPaymentDateTime()));
            transaction.setPaymentMethod(EPaymentMethod.findByDescription(transactionRequest.getPaymentMethod()));
            transaction.setSeats(ESeat.toESeatList(transactionRequest.getSeats()));
            return toTransactionResponse(transactionRepository.saveAndFlush(transaction));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        Double total = 0.0;
        if (DateUtil.isWeekend(transaction.getWatchDate())) {
            total = (transaction.getQty() * transaction.getProductPricing().getWeekendPrice() * (1 + transaction.getTax().getValue()*0.01));
        } else {
            total = (transaction.getQty() * transaction.getProductPricing().getWeekdayPrice() * (1 + transaction.getTax().getValue()*0.01));
        }
        BigDecimal roundedTotal = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(transaction.getId())
                .customerId(transaction.getCustomer().getId())
                .theaterId(transaction.getTheater().getId())
                .studioId(transaction.getStudio().getId())
                .productId(transaction.getProduct().getId())
                .productPricingId(transaction.getProductPricing().getId())
                .productSchedulingId(transaction.getProductScheduling().getId())
                .qty(transaction.getQty())
                .tax(transaction.getTax().getValue())
                .transactionDateTime(transaction.getTransactionDateTime().toString())
                .paymentStatus(transaction.getPaymentStatus().getDescription())
                .paymentDateTime(transaction.getPaymentDateTime().toString())
                .paymentMethod(transaction.getPaymentMethod().getDescription())
                .seats(transaction.getSeats().stream().map(ESeat::getDescription).toList())
                .createdAt(transaction.getCreatedAt().toString())
                .updatedAt(transaction.getUpdatedAt().toString())
                .watchDate(transaction.getWatchDate().toString())
                .total(roundedTotal)
                .build();
        return transactionResponse;
    }

    private void validateRequest(NewTransactionRequest transactionRequest, Theater theater, Studio studio, Product product,
            ProductPricing productPricing, ProductScheduling productScheduling, Integer qty, List<String> seats) {
        if (studio.getTheater().getId() != theater.getId()) throw new RuntimeException(DbBash.THEATER_AND_STUDIO_NOT_MATCH);
        for (String seat : transactionRequest.getSeats()) {
            if (!studio.getSeatLayout().contains(ESeat.findByDescription(seat))) throw new RuntimeException(DbBash.STUDIO_SEAT_NOT_MATCH);
        }
        if (theater.getStudios().stream()
            .noneMatch(studioStream ->
                    studioStream.getId().equals(studio.getId()))) {
                throw new RuntimeException(DbBash.THEATER_AND_STUDIO_NOT_MATCH);
            }
        if (theater.getProducts().stream()
            .noneMatch(productStream ->
                    productStream.getId().equals(product.getId()))) {
                throw new RuntimeException(DbBash.PRODUCT_AND_THEATER_NOT_MATCH);
            }
        if (studio.getProductPricing().stream()
            .noneMatch(productPricingStream ->
                    productPricingStream.getProductIdPricing().getId().equals(product.getId()))) {
                throw new RuntimeException(DbBash.PRODUCT_AND_PRODUCT_PRICING_NOT_MATCH);
            }
        if (studio.getProductScheduling().stream()
            .noneMatch(productSchegetProductSchedulingStream ->
                    productSchegetProductSchedulingStream.getProductIdScheduling().getId().equals(product.getId()))) {
                throw new RuntimeException(DbBash.PRODUCT_AND_PRODUCT_SCHEDULING_NOT_MATCH);
            }
        if (studio.getProductPricing().stream()
            .noneMatch(productPricingStream ->
                    productPricingStream.getId().equals(productPricing.getId()))) {
                throw new RuntimeException(DbBash.PRODUCT_PRICING_AND_STUDIO_NOT_MATCH);
            }
        if (studio.getProductScheduling().stream()
            .noneMatch(productSchedulingStream ->
                    productSchedulingStream.getId().equals(productScheduling.getId()))) {
                throw new RuntimeException(DbBash.PRODUCT_SCHEDULING_AND_STUDIO_NOT_MATCH);
            }
        if (seats.size() != qty) throw new RuntimeException(DbBash.QTY_AND_SEAT_NOT_MATCH);
    }

    private void updateStudioSeatSchedule(Studio studio, ProductScheduling productScheduling, Transaction transaction) {
        System.out.println("ASDASDASDD1");
        StudioSeatSchedule studioSeatSchedule = studioSeatScheduleService.getStudioSeatScheduleByAttribute(studio.getId(), productScheduling.getId());
        System.out.println("ASDASDASDD2" + studioSeatSchedule.getId() + " " + studio.getId() + " " + productScheduling.getId());
        System.out.println("ASDASDASDD3" + studioSeatSchedule.getId() + " " + studioSeatSchedule.getStudio().getId() + " " + studioSeatSchedule.getProductScheduling().getId());
        List<ESeat> newBookedSeat = new ArrayList<>();
        List<ESeat> newAvailableSeat = new ArrayList<>();

        newBookedSeat.addAll(studioSeatSchedule.getBookedSeat());
        newBookedSeat.addAll(transaction.getSeats());
        System.out.println("ASDASDASDD4");

        newAvailableSeat.addAll(studioSeatSchedule.getAvailableSeat());
        newAvailableSeat.removeAll(transaction.getSeats());
        System.out.println("ASDASDASDD5");
        NewStudioSeatScheduleRequest newStudioSeatScheduleRequest = NewStudioSeatScheduleRequest.builder()
                .studioId(studio.getId())
                .bookedSeat(ESeat.toESeatStringList(newBookedSeat))
                .availableSeat(ESeat.toESeatStringList(newAvailableSeat))
                .productSchedulingId(productScheduling.getId())
                .build();
        System.out.println("ASDASDASDD6");
        studioSeatScheduleService.update(null, newStudioSeatScheduleRequest);
        System.out.println("ASDASDASDD7");
    }

}
