package com.flix.flix.service.impl;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EPaymentMethod;
import com.flix.flix.constant.custom_enum.EPaymentStatus;
import com.flix.flix.constant.custom_enum.ERole;
import com.flix.flix.constant.custom_enum.ESeat;
import com.flix.flix.constant.custom_enum.ETax;
import com.flix.flix.entity.AppUser;
import com.flix.flix.entity.Customer;
import com.flix.flix.entity.Employee;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductPricing;
import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.entity.Studio;
import com.flix.flix.entity.StudioSeatSchedule;
import com.flix.flix.entity.Theater;
import com.flix.flix.entity.Transaction;
import com.flix.flix.model.request.NewStudioSeatScheduleRequest;
import com.flix.flix.model.request.NewTransactionRequest;
import com.flix.flix.model.request.search.SearchTransactionRequest;
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
import com.flix.flix.specification.TransactionSpecification;
import com.flix.flix.util.DateUtil;
import com.flix.flix.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private TheaterService theaterService;
    @Mock
    private StudioService studioService;
    @Mock
    private ProductService productService;
    @Mock
    private ProductPricingService productPricingService;
    @Mock
    private ProductSchedulingService productSchedulingService;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private StudioSeatScheduleService studioSeatScheduleService;
    @Mock
    private TokenUtil tokenUtil;
    @Mock
    private HttpServletRequest request;
    @Mock
    private AppUser appUser;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private NewTransactionRequest newTransactionRequest;
    private Theater theater;
    private Studio studio;
    private Product product;
    private ProductPricing productPricing;
    private ProductScheduling productScheduling;
    private AppUser customerAppUser;
    private AppUser cashierAppUser;
    private Customer customer;
    private Employee employee;
    private Transaction transaction;
    private StudioSeatSchedule studioSeatSchedule;


    @BeforeEach
    void setUp() {
        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(anyString())).thenReturn(LocalDate.now());
            mockedDateUtil.when(() -> DateUtil.parseDateTime(anyString())).thenReturn(LocalDateTime.now());
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false); // Default to weekday
        }

        newTransactionRequest = NewTransactionRequest.builder()
                .theaterId("theater1")
                .studioId("studio1")
                .productId("product1")
                .productPricingId("pricing1")
                .productSchedulingId("scheduling1")
                .qty(1)
                .tax(10)
                .transactionDateTime("2025-06-01T10:00:00")
                .paymentDateTime("2025-06-01T10:00:00")
                .paymentMethod("Cash")
                .seats(List.of("A1"))
                .watchDate("2025-06-01")
                .build();

        theater = Theater.builder()
                .id("theater1")
                .studios(List.of())
                .products(List.of())
                .build();
        studio = Studio.builder()
                .id("studio1")
                .theater(theater)
                .seatLayout((List.of(ESeat.SEAT_A1, ESeat.SEAT_A2)))
                .productPricing(List.of())
                .productScheduling(List.of())
                .build();
        product = Product.builder().id("product1").build();
        productPricing = ProductPricing.builder().id("pricing1").weekdayPrice(100.0).weekendPrice(120.0).build();
        productScheduling = ProductScheduling.builder().id("scheduling1").build();

        theater.setStudios(List.of(studio));
        theater.setProducts(List.of(product));
        studio.setProductPricing(List.of(ProductPricing.builder().id("pricing1").productIdPricing(product).build()));
        studio.setProductScheduling(List.of(ProductScheduling.builder().id("scheduling1").productIdScheduling(product).build()));

        customer = Customer.builder().id("customer1").build();
        employee = Employee.builder().id("employee1").build();

        customerAppUser = AppUser.builder()
                .id("appUser1")
                .roles(List.of(ERole.ROLE_CUSTOMER))
                .customer(customer)
                .build();
        cashierAppUser = AppUser.builder()
                .id("appUser2")
                .roles(List.of(ERole.ROLE_CASHIER))
                .employee(employee)
                .build();

        transaction = Transaction.builder()
                .id("trx1")
                .customer(customer)
                .theater(theater)
                .studio(studio)
                .product(product)
                .watchDate(LocalDate.now())
                .productPricing(productPricing)
                .productScheduling(productScheduling)
                .qty(1)
                .tax(ETax.TAX_10)
                .transactionDateTime(LocalDateTime.now())
                .paymentStatus(EPaymentStatus.PAYMENT_STATUS_PENDING)
                .paymentDateTime(LocalDateTime.now())
                .paymentMethod(EPaymentMethod.PAYMENT_Method_CASH)
                .seats(List.of(ESeat.SEAT_A1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusHours(1))
                .build();

        studioSeatSchedule = StudioSeatSchedule.builder()
                .id("sss1")
                .studio(studio)
                .productScheduling(productScheduling)
                .bookedSeat(new ArrayList<>())
                .availableSeat(new ArrayList<>(List.of(ESeat.SEAT_A1, ESeat.SEAT_A2, ESeat.SEAT_A3)))
                .build();
    }

    @Test
    void create_SuccessAsCustomer() {
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);
        when(tokenUtil.getAppUserByToken(request)).thenReturn(customerAppUser);
        when(customerService.getCustomerById(anyString())).thenReturn(customer);
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(transaction);
        when(studioSeatScheduleService.getStudioSeatScheduleByAttribute(anyString(), anyString())).thenReturn(studioSeatSchedule);
        when(studioSeatScheduleService.update(any(), any(NewStudioSeatScheduleRequest.class), any())).thenReturn(null); // Assuming return type for update is not crucial for this test

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(anyString())).thenReturn(LocalDate.now());
            mockedDateUtil.when(() -> DateUtil.parseDateTime(anyString())).thenReturn(LocalDateTime.now());
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            TransactionResponse response = transactionService.create(newTransactionRequest, request);

            assertNotNull(response);
            assertEquals("customer1", response.getCustomerId());
            assertNull(response.getEmployeeCashierId());
            assertEquals("110.00", String.valueOf(response.getTotal())); // 1 * 100 * (1 + 0.10)
            verify(transactionRepository, times(1)).saveAndFlush(any(Transaction.class));
            verify(studioSeatScheduleService, times(1)).update(any(), any(NewStudioSeatScheduleRequest.class), any());
        }
    }

    @Test
    void create_SuccessAsCashier() {
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);
        when(tokenUtil.getAppUserByToken(request)).thenReturn(cashierAppUser);
        when(employeeService.getEmployeeById(anyString())).thenReturn(employee);
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(transaction);
        when(studioSeatScheduleService.getStudioSeatScheduleByAttribute(anyString(), anyString())).thenReturn(studioSeatSchedule);
        when(studioSeatScheduleService.update(any(), any(NewStudioSeatScheduleRequest.class), any())).thenReturn(null); // Assuming return type for update is not crucial for this test

        transaction.setEmployee(employee);
        transaction.setCustomer(null);

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(anyString())).thenReturn(LocalDate.now());
            mockedDateUtil.when(() -> DateUtil.parseDateTime(anyString())).thenReturn(LocalDateTime.now());
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            TransactionResponse response = transactionService.create(newTransactionRequest, request);

            assertNotNull(response);
            assertEquals("employee1", response.getEmployeeCashierId());
            assertNull(response.getCustomerId());
            assertEquals("110.00", String.valueOf(response.getTotal())); // 1 * 100 * (1 + 0.10)
            verify(transactionRepository, times(1)).saveAndFlush(any(Transaction.class));
            verify(studioSeatScheduleService, times(1)).update(any(), any(NewStudioSeatScheduleRequest.class), any());
        }
    }

    @Test
    void create_ThrowExceptionWhenUserIsNotCustomerOrCashier() {
        AppUser guestAppUser = AppUser.builder().id("appUser3").roles(List.of(ERole.ROLE_ADMIN)).build();
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);
        when(tokenUtil.getAppUserByToken(request)).thenReturn(guestAppUser);
        when(studioSeatScheduleService.getStudioSeatScheduleByAttribute(anyString(), anyString())).thenReturn(studioSeatSchedule);
        when(studioSeatScheduleService.update(any(), any(NewStudioSeatScheduleRequest.class), any())).thenReturn(null);

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(anyString())).thenReturn(LocalDate.now());
            mockedDateUtil.when(() -> DateUtil.parseDateTime(anyString())).thenReturn(LocalDateTime.now());
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    transactionService.create(newTransactionRequest, request)
            );
            assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + DbBash.ONLY_CASHIER_OR_CUSTOMER_CAN_CREATE_TRANSACTION, thrown.getMessage());
            verify(transactionRepository, never()).saveAndFlush(any(Transaction.class));
        }
    }

    @Test
    void create_ThrowsRuntimeExceptionOnServiceCallFailure() {
        when(theaterService.getTheaterById(anyString())).thenThrow(new RuntimeException("Theater not found"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.create(newTransactionRequest, request)
        );
        assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + "Theater not found", thrown.getMessage());
        verify(transactionRepository, never()).saveAndFlush(any(Transaction.class));
    }

    @Test
    void create_ThrowsExceptionWhenTheaterAndStudioNotMatch() {
        Studio mismatchedStudio = Studio.builder().id("studio2").theater(Theater.builder().id("theater2").build()).seatLayout((List.of(ESeat.SEAT_A1))).build();
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(mismatchedStudio); // Mismatched studio

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.create(newTransactionRequest, request)
        );
        assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + DbBash.THEATER_AND_STUDIO_NOT_MATCH, thrown.getMessage());
    }

    @Test
    void create_ThrowsExceptionWhenStudioSeatNotMatch() {
        NewTransactionRequest invalidSeatRequest = NewTransactionRequest.builder()
                .theaterId("theater1")
                .studioId("studio1")
                .productId("product1")
                .productPricingId("pricing1")
                .productSchedulingId("scheduling1")
                .qty(1)
                .tax(10)
                .transactionDateTime("2025-06-01T10:00:00")
                .paymentDateTime("2025-06-01T10:00:00")
                .paymentMethod("Cash")
                .seats(List.of("B5")) // Invalid seat
                .watchDate("2025-06-01")
                .build();

        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.create(invalidSeatRequest, request)
        );
        assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + DbBash.STUDIO_SEAT_NOT_MATCH, thrown.getMessage());
    }

    @Test
    void create_ThrowsExceptionWhenTheaterDoesNotContainStudio() {
        theater.setStudios(List.of()); // Theater has no studios
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.create(newTransactionRequest, request)
        );
        assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + DbBash.THEATER_AND_STUDIO_NOT_MATCH, thrown.getMessage());
    }

    @Test
    void create_ThrowsExceptionWhenTheaterDoesNotContainProduct() {
        theater.setProducts(List.of()); // Theater has no products
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.create(newTransactionRequest, request)
        );
        assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + DbBash.PRODUCT_AND_THEATER_NOT_MATCH, thrown.getMessage());
    }

    @Test
    void create_ThrowsExceptionWhenStudioDoesNotContainProductPricingRelatedToProduct() {
        studio.setProductPricing(List.of(ProductPricing.builder().id("pricing2").productIdPricing(Product.builder().id("product2").build()).build())); // Mismatched product pricing
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.create(newTransactionRequest, request)
        );
        assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + DbBash.PRODUCT_AND_PRODUCT_PRICING_NOT_MATCH, thrown.getMessage());
    }

    @Test
    void create_ThrowsExceptionWhenStudioDoesNotContainProductSchedulingRelatedToProduct() {
        studio.setProductScheduling(List.of(ProductScheduling.builder().id("scheduling2").productIdScheduling(Product.builder().id("product2").build()).build())); // Mismatched product scheduling
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.create(newTransactionRequest, request)
        );
        assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + DbBash.PRODUCT_AND_PRODUCT_SCHEDULING_NOT_MATCH, thrown.getMessage());
    }

    @Test
    void create_ThrowsExceptionWhenStudioDoesNotContainProductPricing() {
        studio.setProductPricing(List.of()); // Studio has no product pricing
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.create(newTransactionRequest, request)
        );
        assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + DbBash.PRODUCT_AND_PRODUCT_PRICING_NOT_MATCH, thrown.getMessage());
    }

    @Test
    void create_ThrowsExceptionWhenStudioDoesNotContainProductScheduling() {
        studio.setProductScheduling(List.of()); // Studio has no product scheduling
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.create(newTransactionRequest, request)
        );
        assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + DbBash.PRODUCT_AND_PRODUCT_SCHEDULING_NOT_MATCH, thrown.getMessage());
    }

    @Test
    void create_ThrowsExceptionWhenQtyAndSeatNotMatch() {
        newTransactionRequest.setQty(2); // Qty is 2, but seats list has 1
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.create(newTransactionRequest, request)
        );
        assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + DbBash.QTY_AND_SEAT_NOT_MATCH, thrown.getMessage());
    }

    @Test
    void create_ThrowsExceptionWhenDuplicateSeats() {
        newTransactionRequest.setSeats(List.of("A1", "A1")); // Duplicate seats
        newTransactionRequest.setQty(2);
        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.create(newTransactionRequest, request)
        );
        assertEquals(ApiBash.CREATE_TRANSACTION_FAILED + ": " + DbBash.DUPLICATE_SEAT_REQUEST, thrown.getMessage());
    }


    @Test
    void getAll_SuccessWithDefaultPagination() {
        SearchTransactionRequest searchRequest = new SearchTransactionRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setSortBy("createdAt");
        searchRequest.setDirection("asc");
        searchRequest.setCreatedAtMin("2025-06-01");
        searchRequest.setCreatedAtMax("2025-06-10");
        searchRequest.setUpdatedAtMin("2025-06-01");
        searchRequest.setUpdatedAtMax("2025-06-10");
        searchRequest.setPaymentDateTimeMin("2025-06-01");
        searchRequest.setPaymentDateTimeMax("2025-06-10");
        searchRequest.setProductPriceMin(0.0);
        searchRequest.setProductPriceMax(1000.0);
        searchRequest.setWatchDateMin("2025-06-01");
        searchRequest.setWatchDateMax("2025-06-10");
        searchRequest.setExpirationDateMin("2025-06-01");

        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(transactionPage);

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-01")).thenReturn(LocalDate.of(2025, 6, 1));
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-10")).thenReturn(LocalDate.of(2025, 6, 10));

            Page<TransactionResponse> response = transactionService.getAll(searchRequest);

            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
            verify(transactionRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            assertEquals("110.00", response.getContent().get(0).getTotal().toString());
        }
    }

    @Test
    void getAll_ThrowsExceptionWhenCreatedAtMinAfterMax() {
        SearchTransactionRequest searchRequest = new SearchTransactionRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setSortBy("createdAt");
        searchRequest.setDirection("asc");
        searchRequest.setCreatedAtMin("2025-06-02");
        searchRequest.setCreatedAtMax("2025-06-01");

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-02")).thenReturn(LocalDate.of(2025, 6, 2));
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-01")).thenReturn(LocalDate.of(2025, 6, 1));
            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    transactionService.getAll(searchRequest)
            );
            assertEquals(ApiBash.GET_ALL_TRANSACTION_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
        }
    }

    @Test
    void getAll_ThrowsExceptionWhenUpdatedAtMinAfterMax() {
        SearchTransactionRequest searchRequest = new SearchTransactionRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setSortBy("createdAt");
        searchRequest.setDirection("asc");
        searchRequest.setUpdatedAtMin("2025-06-02");
        searchRequest.setUpdatedAtMax("2025-06-01");

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-02")).thenReturn(LocalDate.of(2025, 6, 2));
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-01")).thenReturn(LocalDate.of(2025, 6, 1));
            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    transactionService.getAll(searchRequest)
            );
            assertEquals(ApiBash.GET_ALL_TRANSACTION_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
        }
    }

    @Test
    void getAll_ThrowsExceptionWhenPaymentDateTimeMinAfterMax() {
        SearchTransactionRequest searchRequest = new SearchTransactionRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setSortBy("createdAt");
        searchRequest.setDirection("asc");
        searchRequest.setPaymentDateTimeMin("2025-06-02");
        searchRequest.setPaymentDateTimeMax("2025-06-01");

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-02")).thenReturn(LocalDate.of(2025, 6, 2));
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-01")).thenReturn(LocalDate.of(2025, 6, 1));
            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    transactionService.getAll(searchRequest)
            );
            assertEquals(ApiBash.GET_ALL_TRANSACTION_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
        }
    }

    @Test
    void getAll_ThrowsExceptionWhenProductPriceMinGreaterThanMax() {
        SearchTransactionRequest searchRequest = new SearchTransactionRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setSortBy("createdAt");
        searchRequest.setDirection("asc");
        searchRequest.setProductPriceMin(100.0);
        searchRequest.setProductPriceMax(50.0);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.getAll(searchRequest)
        );
        assertEquals(ApiBash.GET_ALL_TRANSACTION_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
    }

    @Test
    void getAll_ThrowsExceptionWhenWatchDateMinAfterMax() {
        SearchTransactionRequest searchRequest = new SearchTransactionRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setSortBy("createdAt");
        searchRequest.setDirection("asc");
        searchRequest.setWatchDateMin("2025-06-02");
        searchRequest.setWatchDateMax("2025-06-01");

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-02")).thenReturn(LocalDate.of(2025, 6, 2));
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-01")).thenReturn(LocalDate.of(2025, 6, 1));
            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    transactionService.getAll(searchRequest)
            );
            assertEquals(ApiBash.GET_ALL_TRANSACTION_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
        }
    }

    @Test
    void getAll_ThrowsExceptionWhenExpirationDateMinAfterMax() {
        SearchTransactionRequest searchRequest = new SearchTransactionRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setSortBy("createdAt");
        searchRequest.setDirection("asc");
        searchRequest.setExpirationDateMin("2025-06-02");
        searchRequest.setExpirationDateMax("2025-06-01");

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-02")).thenReturn(LocalDate.of(2025, 6, 2));
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-06-01")).thenReturn(LocalDate.of(2025, 6, 1));
            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    transactionService.getAll(searchRequest)
            );
            assertEquals(ApiBash.GET_ALL_TRANSACTION_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
        }
    }

    @Test
    void getAllByCredentials_AsCustomer() {
        SearchTransactionRequest searchRequest = new SearchTransactionRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setSortBy("createdAt");
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("createdAt");
        searchRequest.setDirection("asc");

        when(tokenUtil.getAppUserByToken(request)).thenReturn(customerAppUser);
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(transactionPage);

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            Page<TransactionResponse> response = transactionService.getAllByCredentials(searchRequest, request);

            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
            assertEquals("customer1", searchRequest.getCustomerName());
            assertNull(searchRequest.getEmployeeId());
        }
    }

    @Test
    void getAllByCredentials_AsCashier() {
        SearchTransactionRequest searchRequest = new SearchTransactionRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setSortBy("createdAt");
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("createdAt");
        searchRequest.setDirection("asc");

        when(tokenUtil.getAppUserByToken(request)).thenReturn(cashierAppUser);
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(transactionPage);

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            Page<TransactionResponse> response = transactionService.getAllByCredentials(searchRequest, request);

            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
            assertNull(searchRequest.getCustomerName());
            assertEquals("employee1", searchRequest.getEmployeeId());
        }
    }

    @Test
    void getAllByCredentials_ThrowsException() {
        SearchTransactionRequest searchRequest = new SearchTransactionRequest();
        when(tokenUtil.getAppUserByToken(request)).thenThrow(new RuntimeException("Token invalid"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.getAllByCredentials(searchRequest, request)
        );
        assertEquals("Token invalid", thrown.getMessage());
    }

    @Test
    void getTransactionById_Success() {
        when(transactionRepository.findById(anyString())).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransactionById("trx1");

        assertNotNull(result);
        assertEquals("trx1", result.getId());
    }

    @Test
    void getTransactionById_NotFound() {
        when(transactionRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.getTransactionById("nonExistentId")
        );
        assertEquals(DbBash.TRANSACTION_NOT_FOUND, thrown.getMessage());
    }

    @Test
    void getById_Success() {
        when(transactionRepository.findById(anyString())).thenReturn(Optional.of(transaction));
        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            TransactionResponse response = transactionService.getById("trx1");

            assertNotNull(response);
            assertEquals("trx1", response.getId());
            assertEquals("110.00", response.getTotal().toString());
        }
    }

    @Test
    void getById_ThrowsException() {
        when(transactionRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.getById("nonExistentId")
        );
        assertEquals(ApiBash.GET_TRANSACTION_FAILED + ": " + DbBash.TRANSACTION_NOT_FOUND, thrown.getMessage());
    }

    @Test
    void updatePaymentStatus_SuccessToSuccess() {
        NewTransactionRequest updateRequest = NewTransactionRequest.builder()
                .paymentStatus("SUCCESS")
                .build();
        transaction.setPaymentStatus(EPaymentStatus.PAYMENT_STATUS_PENDING); // Initial status
        when(transactionRepository.findById(anyString())).thenReturn(Optional.of(transaction));
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(transaction);

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            TransactionResponse response = transactionService.updatePaymentStatus(updateRequest, "trx1", request);

            assertNotNull(response);
            assertEquals(EPaymentStatus.PAYMENT_STATUS_SUCCESS.getDescription(), response.getPaymentStatus());
            verify(transactionRepository, times(1)).saveAndFlush(any(Transaction.class));
            verify(studioSeatScheduleService, never()).update(any(), any(NewStudioSeatScheduleRequest.class), any());
        }
    }

    @Test
    void updatePaymentStatus_SuccessToFailed() {
        NewTransactionRequest updateRequest = NewTransactionRequest.builder()
                .paymentStatus("FAILED")
                .build();
        transaction.setPaymentStatus(EPaymentStatus.PAYMENT_STATUS_PENDING); // Initial status
        when(transactionRepository.findById(anyString())).thenReturn(Optional.of(transaction));
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(transaction);
        when(studioSeatScheduleService.getStudioSeatScheduleByAttribute(anyString(), anyString())).thenReturn(studioSeatSchedule);
        when(studioSeatScheduleService.update(any(), any(NewStudioSeatScheduleRequest.class), any())).thenReturn(null);

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            TransactionResponse response = transactionService.updatePaymentStatus(updateRequest, "trx1", request);

            assertNotNull(response);
            assertEquals(EPaymentStatus.PAYMENT_STATUS_FAILED.getDescription(), response.getPaymentStatus());
            verify(transactionRepository, times(1)).saveAndFlush(any(Transaction.class));
            verify(studioSeatScheduleService, times(1)).update(any(), any(NewStudioSeatScheduleRequest.class), any());

            ArgumentCaptor<NewStudioSeatScheduleRequest> captor = ArgumentCaptor.forClass(NewStudioSeatScheduleRequest.class);
            verify(studioSeatScheduleService).update(any(), captor.capture(), any());
            NewStudioSeatScheduleRequest capturedRequest = captor.getValue();
            assertEquals(List.of(), capturedRequest.getBookedSeat()); // A1 should be removed from booked

        }
    }

    @Test
    void updatePaymentStatus_NoChangeIfStatusNotSuccessOrFailed() {
        NewTransactionRequest updateRequest = NewTransactionRequest.builder()
                .paymentStatus("PENDING") // Status remains pending
                .build();
        transaction.setPaymentStatus(EPaymentStatus.PAYMENT_STATUS_PENDING); // Initial status
        when(transactionRepository.findById(anyString())).thenReturn(Optional.of(transaction));
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(transaction);

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            TransactionResponse response = transactionService.updatePaymentStatus(updateRequest, "trx1", request);

            assertNotNull(response);
            assertEquals(EPaymentStatus.PAYMENT_STATUS_PENDING.getDescription(), response.getPaymentStatus());
            verify(transactionRepository, times(1)).saveAndFlush(any(Transaction.class));
            verify(studioSeatScheduleService, never()).update(any(), any(NewStudioSeatScheduleRequest.class), any());
        }
    }

    @Test
    void updatePaymentStatus_ThrowsException() {
        NewTransactionRequest updateRequest = NewTransactionRequest.builder().paymentStatus("SUCCESS").build();
        when(transactionRepository.findById(anyString())).thenThrow(new RuntimeException("Transaction not found for update"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.updatePaymentStatus(updateRequest, "nonExistentId", request)
        );
        assertEquals(ApiBash.UPDATE_PAYMENT_TRANSACTION_FAILED + ": " + "Transaction not found for update", thrown.getMessage());
        verify(transactionRepository, never()).saveAndFlush(any(Transaction.class));
    }

    @Test
    void updateStudioSeatSchedule_PendingStatus() {
        studioSeatSchedule.setAvailableSeat(new ArrayList<>(List.of(ESeat.SEAT_A1, ESeat.SEAT_A2, ESeat.SEAT_A3)));
        studioSeatSchedule.setBookedSeat(new ArrayList<>());

        transaction.setSeats(List.of(ESeat.SEAT_A1));
        transaction.setPaymentStatus(EPaymentStatus.PAYMENT_STATUS_PENDING);

        when(studioSeatScheduleService.getStudioSeatScheduleByAttribute(studio.getId(), productScheduling.getId()))
                .thenReturn(studioSeatSchedule);
        when(studioSeatScheduleService.update(any(), any(NewStudioSeatScheduleRequest.class), any())).thenReturn(null);

        when(theaterService.getTheaterById(anyString())).thenReturn(theater);
        when(studioService.getStudioById(anyString())).thenReturn(studio);
        when(productService.getProductById(anyString())).thenReturn(product);
        when(productPricingService.getProductPricingById(anyString())).thenReturn(productPricing);
        when(productSchedulingService.getProductSchedulingById(anyString())).thenReturn(productScheduling);
        when(tokenUtil.getAppUserByToken(request)).thenReturn(customerAppUser);
        when(customerService.getCustomerById(anyString())).thenReturn(customer);
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(transaction);

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(anyString())).thenReturn(LocalDate.now());
            mockedDateUtil.when(() -> DateUtil.parseDateTime(anyString())).thenReturn(LocalDateTime.now());
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            transactionService.create(newTransactionRequest, request);

            ArgumentCaptor<NewStudioSeatScheduleRequest> captor = ArgumentCaptor.forClass(NewStudioSeatScheduleRequest.class);
            verify(studioSeatScheduleService).update(any(), captor.capture(), any());
            NewStudioSeatScheduleRequest capturedRequest = captor.getValue();

            assertEquals(List.of("A1"), capturedRequest.getBookedSeat());
            assertEquals(List.of("A2", "A3"), capturedRequest.getAvailableSeat());
        }
    }

    @Test
    void updateStudioSeatSchedule_FailedStatus() {
        studioSeatSchedule.setAvailableSeat(new ArrayList<>(List.of(ESeat.SEAT_A2, ESeat.SEAT_A3)));
        studioSeatSchedule.setBookedSeat(new ArrayList<>(List.of(ESeat.SEAT_A1)));

        transaction.setSeats(List.of(ESeat.SEAT_A1));
        transaction.setPaymentStatus(EPaymentStatus.PAYMENT_STATUS_FAILED); // Change payment status to FAILED

        when(studioSeatScheduleService.getStudioSeatScheduleByAttribute(studio.getId(), productScheduling.getId()))
                .thenReturn(studioSeatSchedule);
        when(studioSeatScheduleService.update(any(), any(NewStudioSeatScheduleRequest.class), any())).thenReturn(null);
        when(transactionRepository.findById(anyString())).thenReturn(Optional.of(transaction)); // For updatePaymentStatus
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(transaction); // For updatePaymentStatus

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.isWeekend(any(LocalDate.class))).thenReturn(false);

            NewTransactionRequest updateRequest = NewTransactionRequest.builder()
                    .paymentStatus("FAILED")
                    .build();
            transactionService.updatePaymentStatus(updateRequest, transaction.getId(), request);

            ArgumentCaptor<NewStudioSeatScheduleRequest> captor = ArgumentCaptor.forClass(NewStudioSeatScheduleRequest.class);
            verify(studioSeatScheduleService).update(any(), captor.capture(), any());
            NewStudioSeatScheduleRequest capturedRequest = captor.getValue();

            assertEquals(List.of(), capturedRequest.getBookedSeat());
            assertEquals(List.of("A2", "A3", "A1"), capturedRequest.getAvailableSeat()); // Order might differ, so check content
        }
    }
}