package com.flix.flix.service.impl;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ECountry;
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
import com.flix.flix.service.FavGenreService;
import com.flix.flix.service.ProductService;
import com.flix.flix.specification.CustomerSpecification;
import com.flix.flix.util.DateUtil;
import com.flix.flix.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private FavGenreService favGenreService;
    @Mock
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private AppUserService appUserService;
    @Mock
    private TokenUtil tokenUtil;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private UpdateCustomerRequest updateRequest;
    @Mock
    private AppUser appUser;
    @Mock
    private Customer customer;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private NewCustomerRequest newCustomerRequest;
    private UpdateCustomerRequest updateCustomerRequest;
    private Customer testCustomer;
    private AppUser testAppUser;
    private FavGenre favGenreAction, favGenreComedy;
    private Product productLike1, productLike2, productDislike1;

    @BeforeEach
    void setUp() {
        testAppUser = AppUser.builder()
                .id("appuser-1")
                .username("janedoe")
                .email("jane@example.com")
                .password("hashed_password")
                .roles(List.of(ERole.ROLE_CUSTOMER))
                .build();

        testCustomer = Customer.builder()
                .id("cust-1")
                .fullname("John Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .country(ECountry.COUNTRY_INDONESIA)
                .city("Jakarta")
                .phoneNumber("08123456789")
                .gender(EGender.GENDER_MALE)
                .registrationDate(LocalDate.of(2023, 1, 1))
                .lastLogin(LocalDate.now())
                .appUser(testAppUser)
                .build();

        testAppUser.setCustomer(testCustomer);

        favGenreAction = FavGenre.builder().id("fg-1").favGenre(EGenre.GENRE_ACTION).customer(testCustomer).build();
        favGenreComedy = FavGenre.builder().id("fg-2").favGenre(EGenre.GENRE_COMEDY).customer(testCustomer).build();
        testCustomer.setFavGenre(new ArrayList<>(Arrays.asList(favGenreAction, favGenreComedy)));

        productLike1 = Product.builder().id("prod-L1").title("Liked Movie 1").build();
        productLike2 = Product.builder().id("prod-L2").title("Liked Movie 2").build();
        productDislike1 = Product.builder().id("prod-D1").title("Disliked Movie 1").build();

        productLike1.setCustomerLike(new ArrayList<>(Arrays.asList(testCustomer)));
        productLike2.setCustomerLike(new ArrayList<>(Arrays.asList(testCustomer)));
        productDislike1.setCustomerDislike(new ArrayList<>(Arrays.asList(testCustomer)));

        testCustomer.setLikeProduct(new ArrayList<>(Arrays.asList(productLike1, productLike2)));
        testCustomer.setDislikeProduct(new ArrayList<>(Arrays.asList(productDislike1)));

        newCustomerRequest = NewCustomerRequest.builder()
                .fullname("Jane Doe")
                .birthDate("1995-05-10")
                .country("INDONESIA")
                .city("Bandung")
                .phoneNumber("08198765432")
                .gender("FEMALE")
                .email("jane@example.com")
                .username("janedoe")
                .password("password123")
                .favGenre(List.of("DRAMA", "HORROR"))
                .likeProductId(List.of("new-prod-L1"))
                .dislikeProductId(List.of("new-prod-D1"))
                .build();

        updateCustomerRequest = UpdateCustomerRequest.builder()
                .fullname("Johnathan Doe")
                .birthDate("1990-01-01")
                .country("INDONESIA")
                .city("Surabaya")
                .phoneNumber("08111111111")
                .gender("MALE")
                .registrationDate("2023-01-01")
                .lastLogin(LocalDate.now().toString())
                .favGenre(Arrays.asList("ACTION", "THRILLER")) // Update fav genres
                .likeProductId(Arrays.asList("prod-L1", "prod-L3")) // Update liked products
                .dislikeProductId(Collections.singletonList("prod-D2")) // Update disliked products
                .build();
    }

    @Test
    void create_shouldReturnCustomerResponse_whenSuccessful() {
        when(appUserService.signup(any(NewUserRequest.class)))
                .thenReturn(SignupResponse.builder().accountId("appuser-1").build());
        when(appUserService.getAppUserById("appuser-1")).thenReturn(testAppUser);
        when(customerRepository.saveAndFlush(any(Customer.class))).thenAnswer(invocation -> {
            Customer savedCustomer = invocation.getArgument(0);
            savedCustomer.setId("new-cust-id"); // Simulate ID generation
            return savedCustomer;
        });

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(newCustomerRequest.getBirthDate()))
                    .thenReturn(LocalDate.of(1995, 5, 10));

            CustomerResponse result = customerService.create(newCustomerRequest);

            assertNotNull(result);
            assertEquals("new-cust-id", result.getId());
            assertEquals(newCustomerRequest.getFullname(), result.getFullname());
            assertEquals(newCustomerRequest.getEmail(), result.getUserAppEmail());
            assertEquals(newCustomerRequest.getUsername(), result.getUserAppUsername());
            assertEquals(ECountry.COUNTRY_INDONESIA.getDescription(), result.getCountry());
            assertEquals(EGender.GENDER_FEMALE.getDescription(), result.getGender());
            assertNotNull(result.getRegistrationDate());
            assertNotNull(result.getLastLogin());

            verify(appUserService, times(1)).signup(any(NewUserRequest.class));
            verify(appUserService, times(1)).getAppUserById("appuser-1");
            verify(customerRepository, times(1)).saveAndFlush(any(Customer.class));
        }
    }

    @Test
    void create_shouldThrowRuntimeException_whenDuplicateFavGenreInRequest() {
        newCustomerRequest.setFavGenre(Arrays.asList("DRAMA", "DRAMA")); // Duplicate fav genre

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.create(newCustomerRequest));

        assertEquals(ApiBash.CREATE_CUSTOMER_FAILED + ": " + DbBash.DUPLICATE_FAV_GENRE_REQUEST, thrown.getMessage());
        verifyNoInteractions(appUserService, customerRepository);
    }

    @Test
    void create_shouldThrowRuntimeException_whenDuplicateLikeProductIdInRequest() {
        newCustomerRequest.setLikeProductId(Arrays.asList("prod-1", "prod-1")); // Duplicate product

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.create(newCustomerRequest));

        assertEquals(ApiBash.CREATE_CUSTOMER_FAILED + ": " + DbBash.DUPLICATE_PRODUCT_REQUEST, thrown.getMessage());
        verifyNoInteractions(appUserService, customerRepository);
    }

    @Test
    void create_shouldThrowRuntimeException_whenDuplicateDislikeProductIdInRequest() {
        newCustomerRequest.setDislikeProductId(Arrays.asList("prod-1", "prod-1")); // Duplicate product

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.create(newCustomerRequest));

        assertEquals(ApiBash.CREATE_CUSTOMER_FAILED + ": " + DbBash.DUPLICATE_PRODUCT_REQUEST, thrown.getMessage());
        verifyNoInteractions(appUserService, customerRepository);
    }

    @Test
    void create_shouldThrowRuntimeException_whenAppUserServiceSignupFails() {
        when(appUserService.signup(any(NewUserRequest.class))).thenThrow(new RuntimeException("Signup failed"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.create(newCustomerRequest));

        assertEquals(ApiBash.CREATE_CUSTOMER_FAILED + ": Signup failed", thrown.getMessage());
        verify(appUserService, times(1)).signup(any(NewUserRequest.class));
        verifyNoMoreInteractions(appUserService, customerRepository);
    }

    @Test
    void create_shouldThrowRuntimeException_whenAppUserServiceGetAppUserByIdFails() {
        when(appUserService.signup(any(NewUserRequest.class)))
                .thenReturn(SignupResponse.builder().accountId("appuser-1").build());
        when(appUserService.getAppUserById("appuser-1")).thenThrow(new RuntimeException("User not found"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.create(newCustomerRequest));

        assertEquals(ApiBash.CREATE_CUSTOMER_FAILED + ": User not found", thrown.getMessage());
        verify(appUserService, times(1)).signup(any(NewUserRequest.class));
        verify(appUserService, times(1)).getAppUserById("appuser-1");
        verifyNoMoreInteractions(appUserService, customerRepository);
    }

    @Test
    void create_shouldThrowRuntimeException_whenCustomerRepositorySaveFails() {
        when(appUserService.signup(any(NewUserRequest.class)))
                .thenReturn(SignupResponse.builder().accountId("appuser-1").build());
        when(appUserService.getAppUserById("appuser-1")).thenReturn(testAppUser);
        when(customerRepository.saveAndFlush(any(Customer.class))).thenThrow(new RuntimeException("DB error"));

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(newCustomerRequest.getBirthDate()))
                    .thenReturn(LocalDate.of(1995, 5, 10));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    customerService.create(newCustomerRequest));

            assertEquals(ApiBash.CREATE_CUSTOMER_FAILED + ": DB error", thrown.getMessage());
            verify(appUserService, times(1)).signup(any(NewUserRequest.class));
            verify(appUserService, times(1)).getAppUserById("appuser-1");
            verify(customerRepository, times(1)).saveAndFlush(any(Customer.class));
        }
    }

    @Test
    void getAll_shouldReturnPageOfCustomerResponses_whenSuccessful() {
        SearchCustomerRequest searchRequest = new SearchCustomerRequest();
        searchRequest.setPage(1);
        searchRequest.setSize(10);
        searchRequest.setSortBy("fullname");
        searchRequest.setDirection("asc");
        searchRequest.setFullname("John");
        searchRequest.setRegistrationDateMax("2025-01-02");
        searchRequest.setRegistrationDateMin("2025-01-01");
        searchRequest.setLastLoginMax("2025-01-02");
        searchRequest.setLastLoginMin("2025-01-01");
        searchRequest.setBirthDateMax("2025-01-02");
        searchRequest.setBirthDateMin("2025-01-01");

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "fullname"));
        Page<Customer> customerPage = new PageImpl<>(Collections.singletonList(testCustomer), pageable, 1);

        try (MockedStatic<CustomerSpecification> mockedSpecification = mockStatic(CustomerSpecification.class)) {
            mockedSpecification.when(() -> CustomerSpecification.getSpecification(any(SearchCustomerRequest.class)))
                    .thenReturn(mock(Specification.class));

            when(customerRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(customerPage);

            Page<CustomerResponse> result = customerService.getAll(searchRequest);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testCustomer.getId(), result.getContent().get(0).getId());
            assertEquals(testCustomer.getFullname(), result.getContent().get(0).getFullname());

            mockedSpecification.verify(() -> CustomerSpecification.getSpecification(searchRequest), times(1));
            verify(customerRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    void getAll_shouldSetDefaultPageAndSize() {
        SearchCustomerRequest searchRequest = new SearchCustomerRequest();
        searchRequest.setSortBy("id");
        searchRequest.setDirection("asc");
        searchRequest.setPage(0);
        searchRequest.setSize(0);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id")); // Default sort is "id"
        Page<Customer> customerPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        try (MockedStatic<CustomerSpecification> mockedSpecification = mockStatic(CustomerSpecification.class)) {
            mockedSpecification.when(() -> CustomerSpecification.getSpecification(any(SearchCustomerRequest.class)))
                    .thenReturn(mock(Specification.class));

            when(customerRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(customerPage);

            customerService.getAll(searchRequest);

            assertEquals(1, searchRequest.getPage()); // Should be set to 1
            assertEquals(10, searchRequest.getSize()); // Should be set to 10

        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenRegistrationDateMinMaxInvalid() {
        SearchCustomerRequest searchRequest = new SearchCustomerRequest();
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("fullname");
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setRegistrationDateMin("2025-01-02");
        searchRequest.setRegistrationDateMax("2025-01-01"); // Invalid range

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-01-02")).thenReturn(LocalDate.of(2025, 1, 2));
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-01-01")).thenReturn(LocalDate.of(2025, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    customerService.getAll(searchRequest));

            assertEquals(ApiBash.GET_ALL_CUSTOMER_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
            verifyNoInteractions(customerRepository);
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenLastLoginMinMaxInvalid() {
        SearchCustomerRequest searchRequest = new SearchCustomerRequest();
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("fullname");
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setLastLoginMin("2025-01-02");
        searchRequest.setLastLoginMax("2025-01-01"); // Invalid range

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-01-02")).thenReturn(LocalDate.of(2025, 1, 2));
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-01-01")).thenReturn(LocalDate.of(2025, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    customerService.getAll(searchRequest));

            assertEquals(ApiBash.GET_ALL_CUSTOMER_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
            verifyNoInteractions(customerRepository);
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenBirthDateMinMaxInvalid() {
        SearchCustomerRequest searchRequest = new SearchCustomerRequest();
        searchRequest.setDirection("asc");
        searchRequest.setSortBy("fullname");
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setBirthDateMin("2025-01-02");
        searchRequest.setBirthDateMax("2025-01-01"); // Invalid range

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-01-02")).thenReturn(LocalDate.of(2025, 1, 2));
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-01-01")).thenReturn(LocalDate.of(2025, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    customerService.getAll(searchRequest));

            assertEquals(ApiBash.GET_ALL_CUSTOMER_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
            verifyNoInteractions(customerRepository);
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenRepositoryFails() {
        SearchCustomerRequest searchRequest = new SearchCustomerRequest();
        searchRequest.setPage(1);
        searchRequest.setSize(10);
        searchRequest.setSortBy("id");
        searchRequest.setDirection("asc");

        try (MockedStatic<CustomerSpecification> mockedSpecification = mockStatic(CustomerSpecification.class)) {
            mockedSpecification.when(() -> CustomerSpecification.getSpecification(any(SearchCustomerRequest.class)))
                    .thenReturn(mock(Specification.class));

            when(customerRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenThrow(new RuntimeException("DB error during findAll"));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    customerService.getAll(searchRequest));

            assertEquals(ApiBash.GET_ALL_CUSTOMER_FAILED + ": DB error during findAll", thrown.getMessage());
            verify(customerRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    void getById_shouldReturnCustomerResponse_whenFound() {
        when(customerRepository.findById(testCustomer.getId())).thenReturn(Optional.of(testCustomer));

        CustomerResponse result = customerService.getById(testCustomer.getId());

        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testCustomer.getFullname(), result.getFullname());
        assertEquals(testCustomer.getAppUser().getEmail(), result.getUserAppEmail());
        assertEquals(testCustomer.getFavGenre().stream().map(fg -> fg.getFavGenre().getDescription()).collect(Collectors.toList()), result.getFavGenre());
        assertEquals(testCustomer.getLikeProduct().stream().map(Product::getId).collect(Collectors.toList()), result.getLikeProductId());
        assertEquals(testCustomer.getDislikeProduct().stream().map(Product::getId).collect(Collectors.toList()), result.getDislikeProductId());

        verify(customerRepository, times(1)).findById(testCustomer.getId());
    }

    @Test
    void getById_shouldThrowRuntimeException_whenNotFound() {
        when(customerRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.getById("non-existent-id"));

        assertEquals(ApiBash.GET_CUSTOMER_FAILED + ": " + DbBash.CUSTOMER_NOT_FOUND, thrown.getMessage());
        verify(customerRepository, times(1)).findById(anyString());
    }

    @Test
    void getCustomerById_shouldReturnCustomer_whenFound() {
        when(customerRepository.findById(testCustomer.getId())).thenReturn(Optional.of(testCustomer));

        Customer result = customerService.getCustomerById(testCustomer.getId());

        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        verify(customerRepository, times(1)).findById(testCustomer.getId());
    }

    @Test
    void getCustomerById_shouldThrowRuntimeException_whenNotFound() {
        when(customerRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.getCustomerById("non-existent-id"));

        assertEquals(DbBash.CUSTOMER_NOT_FOUND, thrown.getMessage());
        verify(customerRepository, times(1)).findById(anyString());
    }

    @Test
    void testUpdateCustomer_Success() {

        when(updateRequest.getFullname()).thenReturn("John Doe");
        when(updateRequest.getCountry()).thenReturn("Indonesia");
        when(updateRequest.getBirthDate()).thenReturn("2000-01-01");
        when(updateRequest.getPhoneNumber()).thenReturn("08123456789");
        when(updateRequest.getCity()).thenReturn("Bandung");
        when(updateRequest.getGender()).thenReturn("Male");
        when(updateRequest.getRegistrationDate()).thenReturn("2024-01-01");
        when(updateRequest.getLastLogin()).thenReturn("2024-06-01");
        when(updateRequest.getFavGenre()).thenReturn(List.of("Action", "Drama"));
        when(updateRequest.getLikeProductId()).thenReturn(List.of("product-1"));
        when(updateRequest.getDislikeProductId()).thenReturn(List.of("product-2"));

        Customer customer = new Customer();
        customer.setId("customer-1");
        customer.setFavGenre(new ArrayList<>());
        customer.setLikeProduct(new ArrayList<>());
        customer.setDislikeProduct(new ArrayList<>());
        customer.setAppUser(AppUser.builder().id("appuser-1").username("janedoe").email("jane@example.com").password("hashed_password").roles(List.of(ERole.ROLE_CUSTOMER)).build()); // untuk coverage getAppUser()

        when(customerRepository.findById("customer-1")).thenReturn(Optional.of(customer));

        Product product1 = mock(Product.class);
        when(productService.getProductById("product-1")).thenReturn(product1);
        when(product1.containsCustomerLike(customer)).thenReturn(false);
        when(product1.containsCustomerDislike(customer)).thenReturn(true);
        when(product1.getCustomerDislike()).thenReturn(new ArrayList<>());
        when(product1.getCustomerLike()).thenReturn(new ArrayList<>());
        when(productRepository.save(product1)).thenReturn(product1);

        Product product2 = mock(Product.class);
        when(productService.getProductById("product-2")).thenReturn(product2);
        when(product2.containsCustomerDislike(customer)).thenReturn(false);
        when(product2.containsCustomerLike(customer)).thenReturn(true);
        when(product2.getCustomerLike()).thenReturn(new ArrayList<>());
        when(product2.getCustomerDislike()).thenReturn(new ArrayList<>());
        when(productRepository.save(product2)).thenReturn(product2);

        when(favGenreService.create(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.update("customer-1", updateRequest);

        assertNotNull(response);
        verify(customerRepository).saveAndFlush(customer);
    }

    @Test
    void update_shouldHandleEmptyFavGenreAndProductsInRequest() {
        updateCustomerRequest.setFavGenre(Collections.emptyList());
        updateCustomerRequest.setLikeProductId(Collections.emptyList());
        updateCustomerRequest.setDislikeProductId(Collections.emptyList());

        when(customerRepository.findById(testCustomer.getId())).thenReturn(Optional.of(testCustomer));

        CustomerResponse result = customerService.update(testCustomer.getId(), updateCustomerRequest);

        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testCustomer.getFullname(), result.getFullname());
        assertEquals(testCustomer.getAppUser().getEmail(), result.getUserAppEmail());
        assertEquals(testCustomer.getFavGenre().stream().map(fg -> fg.getFavGenre().getDescription()).collect(Collectors.toList()), result.getFavGenre());
        assertEquals(testCustomer.getLikeProduct().stream().map(Product::getId).collect(Collectors.toList()), result.getLikeProductId());
        assertEquals(testCustomer.getDislikeProduct().stream().map(Product::getId).collect(Collectors.toList()), result.getDislikeProductId());
    }


    @Test
    void update_shouldThrowRuntimeException_whenCustomerNotFound() {
        when(customerRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.update("non-existent-id", updateCustomerRequest));

        assertEquals(ApiBash.UPDATE_CUSTOMER_FAILED + ": " + DbBash.CUSTOMER_NOT_FOUND, thrown.getMessage());
        verify(customerRepository, times(1)).findById(anyString());
        verifyNoInteractions(favGenreService, productService, productRepository);
    }

    @Test
    void update_shouldThrowRuntimeException_whenDuplicateFavGenreInRequest() {
        updateCustomerRequest.setFavGenre(Arrays.asList("Action", "Action")); // Duplicate
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.update(testCustomer.getId(), updateCustomerRequest));

        assertEquals(ApiBash.UPDATE_CUSTOMER_FAILED + ": " + DbBash.DUPLICATE_FAV_GENRE_REQUEST, thrown.getMessage());
        verifyNoInteractions(favGenreService, productService, productRepository);
    }

    @Test
    void update_shouldThrowRuntimeException_whenDuplicateLikeProductIdInRequest() {
        updateCustomerRequest.setLikeProductId(Arrays.asList("prod-1", "prod-1")); // Duplicate

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.update(testCustomer.getId(), updateCustomerRequest));

        assertEquals(ApiBash.UPDATE_CUSTOMER_FAILED + ": " + DbBash.DUPLICATE_PRODUCT_REQUEST, thrown.getMessage());
        verifyNoInteractions(favGenreService, productService, productRepository);
    }

    @Test
    void update_shouldThrowRuntimeException_whenDuplicateDislikeProductIdInRequest() {
        updateCustomerRequest.setDislikeProductId(Arrays.asList("prod-1", "prod-1")); // Duplicate

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.update(testCustomer.getId(), updateCustomerRequest));

        assertEquals(ApiBash.UPDATE_CUSTOMER_FAILED + ": " + DbBash.DUPLICATE_PRODUCT_REQUEST, thrown.getMessage());
        verifyNoInteractions(favGenreService, productService, productRepository);
    }

    @Test
    void update_shouldThrowRuntimeException_whenLikeDislikeConflict() {
        updateCustomerRequest.setLikeProductId(Collections.singletonList("prod-1")); // Like a disliked product
        updateCustomerRequest.setDislikeProductId(Collections.singletonList("prod-1")); // Dislike a previously liked product

        when(customerRepository.findById(testCustomer.getId())).thenReturn(Optional.of(testCustomer));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.update(testCustomer.getId(), updateCustomerRequest));

        assertEquals(ApiBash.UPDATE_CUSTOMER_FAILED + ": " + DbBash.LIKE_DISLIKE_CONFLICT, thrown.getMessage());
    }

    @Test
    void update_shouldThrowRuntimeException_whenProductServiceGetProductByIdFails() {
        updateCustomerRequest.setLikeProductId(Collections.singletonList("non-existent-prod"));
        when(customerRepository.findById(testCustomer.getId())).thenReturn(Optional.of(testCustomer));
        when(productService.getProductById("non-existent-prod")).thenThrow(new RuntimeException("Product not found"));

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(anyString()))
                    .thenAnswer(invocation -> LocalDate.parse(invocation.getArgument(0)));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    customerService.update(testCustomer.getId(), updateCustomerRequest));

            assertEquals(ApiBash.UPDATE_CUSTOMER_FAILED + ": Product not found", thrown.getMessage());
            verify(customerRepository, times(1)).findById(testCustomer.getId());
        }
    }

    @Test
    void getByCredentials_shouldReturnCustomerResponse_whenTokenIsValid() {
        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenReturn(testAppUser);
        testAppUser.setCustomer(testCustomer);

        CustomerResponse result = customerService.getByCredentials(httpServletRequest);

        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testAppUser.getUsername(), result.getUserAppUsername());
        verify(tokenUtil, times(1)).getAppUserByToken(httpServletRequest);
    }

    @Test
    void getByCredentials_shouldThrowRuntimeException_whenTokenIsInvalid() {
        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenThrow(new RuntimeException("Invalid token"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.getByCredentials(httpServletRequest));

        assertEquals(ApiBash.GET_CUSTOMER_FAILED + ": Invalid token", thrown.getMessage());
        verify(tokenUtil, times(1)).getAppUserByToken(httpServletRequest);
    }

    @Test
    void getByCredentials_shouldThrowRuntimeException_whenAppUserHasNoCustomer() {
        AppUser appUserWithoutCustomer = AppUser.builder()
                .id("appuser-2")
                .username("no_cust")
                .email("no_cust@example.com")
                .password("hashed_password")
                .roles(List.of(ERole.ROLE_CUSTOMER))
                .customer(null) // No customer linked
                .build();
        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenReturn(appUserWithoutCustomer);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.getByCredentials(httpServletRequest));

        assertTrue(thrown.getMessage().startsWith(ApiBash.GET_CUSTOMER_FAILED));
        verify(tokenUtil, times(1)).getAppUserByToken(httpServletRequest);
    }

    @Test
    void testUpdateByCredentials_Success() {

        customerService = Mockito.spy(customerService);

        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenReturn(appUser);
        when(appUser.getRoles()).thenReturn(List.of(ERole.ROLE_CUSTOMER));
        when(appUser.getCustomer()).thenReturn(customer);
        when(customer.getId()).thenReturn("customer-1");

        CustomerResponse mockedResponse = new CustomerResponse();
        doReturn(mockedResponse).when(customerService).update("customer-1", updateCustomerRequest);

        CustomerResponse response = customerService.updateByCredentials(httpServletRequest, updateCustomerRequest);

        assertNotNull(response);
        verify(customerService).update("customer-1", updateCustomerRequest);
    }

    @Test
    void testUpdateByCredentials_Unauthorized() {
        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenReturn(appUser);
        when(appUser.getRoles()).thenReturn(List.of(ERole.ROLE_ADMIN)); // Bukan CUSTOMER

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            customerService.updateByCredentials(httpServletRequest, updateCustomerRequest)
        );

        assertEquals(DbBash.UNAUTHORIZED, ex.getMessage());
    }

    @Test
    void testUpdateByCredentials_ExceptionThrown() {
        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenThrow(new RuntimeException("Invalid token"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            customerService.updateByCredentials(httpServletRequest, updateCustomerRequest)
        );

        assertEquals("Invalid token", ex.getMessage());
    }

    @Test
    void delete_shouldCallRepositoryDelete_whenFound() {
        when(customerRepository.findById(testCustomer.getId())).thenReturn(Optional.of(testCustomer));
        doNothing().when(customerRepository).deleteById(testCustomer.getId());

        assertDoesNotThrow(() -> customerService.delete(testCustomer.getId()));

        verify(customerRepository, times(1)).findById(testCustomer.getId());
        verify(customerRepository, times(1)).deleteById(testCustomer.getId());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenNotFound() {
        when(customerRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.delete("non-existent-id"));

        assertEquals(ApiBash.DELETE_CUSTOMER_FAILED + ": " + DbBash.CUSTOMER_NOT_FOUND, thrown.getMessage());
        verify(customerRepository, times(1)).findById(anyString());
        verify(customerRepository, never()).deleteById(anyString());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenRepositoryDeleteFails() {
        when(customerRepository.findById(testCustomer.getId())).thenReturn(Optional.of(testCustomer));
        doThrow(new RuntimeException("DB delete error")).when(customerRepository).deleteById(testCustomer.getId());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                customerService.delete(testCustomer.getId()));

        assertEquals(ApiBash.DELETE_CUSTOMER_FAILED + ": DB delete error", thrown.getMessage());
        verify(customerRepository, times(1)).findById(testCustomer.getId());
        verify(customerRepository, times(1)).deleteById(testCustomer.getId());
    }

}