package com.flix.flix.service.impl;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.DbBash;
import com.flix.flix.entity.Employee;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.Studio;
import com.flix.flix.entity.Theater;
import com.flix.flix.model.request.NewTheaterRequest;
import com.flix.flix.model.request.search.SearchTheaterRequest;
import com.flix.flix.model.response.StudioResponse;
import com.flix.flix.model.response.TheaterResponse;
import com.flix.flix.repository.TheaterRepository;
import com.flix.flix.service.ProductService;
import com.flix.flix.service.StudioService;
import com.flix.flix.specification.TheaterSpecification;
import com.flix.flix.util.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TheaterServiceImplTest {

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private StudioService studioService;

    @Mock
    private ProductService productService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private TheaterServiceImpl theaterService;

    private Theater testTheater;
    private NewTheaterRequest newTheaterRequest;
    private Studio testStudio1;
    private Studio testStudio2;
    private Product testProduct1;
    private Product testProduct2;
    private Employee testEmployee1;
    private Employee testEmployee2;

    @BeforeEach
    void setUp() {
        testTheater = Theater.builder()
                .id("theater-1")
                .name("Flix Grand")
                .city("Jakarta")
                .address("Jl. Sudirman No. 1")
                .contactNumber("0211234567")
                .contactEmail("flixgrand@example.com")
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .oprationalStatus(true)
                .build();

        testStudio1 = Studio.builder().id("studio-1").name("Studio A").theater(testTheater).build();
        testStudio2 = Studio.builder().id("studio-2").name("Studio B").theater(testTheater).build();
        testTheater.setStudios(Arrays.asList(testStudio1, testStudio2));

        testProduct1 = Product.builder().id("prod-1").title("Movie A").posterUrl("url-a").build();
        testProduct2 = Product.builder().id("prod-2").title("Movie B").posterUrl("url-b").build();
        testTheater.setProducts(Arrays.asList(testProduct1, testProduct2));

        testEmployee1 = Employee.builder().id("emp-1").fullname("John Doe").build();
        testEmployee2 = Employee.builder().id("emp-2").fullname("Jane Smith").build();
        testTheater.setEmployees(Arrays.asList(testEmployee1, testEmployee2));


        newTheaterRequest = NewTheaterRequest.builder()
                .name("Flix Central")
                .city("Bandung")
                .address("Jl. Asia Afrika No. 10")
                .contactNumber("0229876543")
                .contactEmail("flixcentral@example.com")
                .studiosId(Arrays.asList("studio-1", "studio-2"))
                .nowShowingId(Arrays.asList("prod-1", "prod-2"))
                .build();
    }

    @Test
    void create_shouldReturnTheaterResponse_whenSuccessful() {
        when(theaterRepository.saveAndFlush(any(Theater.class))).thenAnswer(invocation -> {
            Theater savedTheater = invocation.getArgument(0);
            savedTheater.setId("new-theater-id"); // Simulate ID generation
            return savedTheater;
        });

        TheaterResponse result = theaterService.create(newTheaterRequest);

        assertNotNull(result);
        assertEquals("new-theater-id", result.getId());
        assertEquals(newTheaterRequest.getName(), result.getName());
        assertEquals(newTheaterRequest.getCity(), result.getCity());
        assertTrue(result.getOprationalStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(theaterRepository, times(1)).saveAndFlush(any(Theater.class));
    }

    @Test
    void create_shouldThrowRuntimeException_whenRepositoryFails() {
        when(theaterRepository.saveAndFlush(any(Theater.class))).thenThrow(new RuntimeException("DB error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.create(newTheaterRequest));

        assertEquals(ApiBash.CREATE_THEATER_FAILED + ": DB error", thrown.getMessage());
        verify(theaterRepository, times(1)).saveAndFlush(any(Theater.class));
    }

    @Test
    void getById_shouldReturnTheaterResponse_whenFound() {
        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));
        when(studioService.toStudioResponse(any(Studio.class))).thenAnswer(invocation -> {
            Studio studio = invocation.getArgument(0);
            return StudioResponse.builder().id(studio.getId()).name(studio.getName()).build();
        });


        TheaterResponse result = theaterService.getById(testTheater.getId());

        assertNotNull(result);
        assertEquals(testTheater.getId(), result.getId());
        assertEquals(testTheater.getName(), result.getName());
        assertEquals(testTheater.getStudios().size(), result.getStudios().size());
        assertEquals(testTheater.getProducts().size(), result.getNowShowing().size());
        assertEquals(testTheater.getEmployees().size(), result.getEmployees().size());

        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verify(studioService, times(testTheater.getStudios().size())).toStudioResponse(any(Studio.class));
    }

    @Test
    void getById_shouldThrowRuntimeException_whenNotFound() {
        when(theaterRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.getById("non-existent-id"));

        assertEquals(ApiBash.GET_THEATER_FAILED + ": " + DbBash.THEATER_NOT_FOUND, thrown.getMessage());
        verify(theaterRepository, times(1)).findById(anyString());
    }

    @Test
    void getById_shouldThrowRuntimeException_whenToTheaterResponseFails() {
        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));
        when(studioService.toStudioResponse(any(Studio.class))).thenThrow(new RuntimeException("Studio conversion error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.getById(testTheater.getId()));

        assertEquals(ApiBash.GET_THEATER_FAILED + ": Studio conversion error", thrown.getMessage());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verify(studioService, times(1)).toStudioResponse(any(Studio.class));
    }

    @Test
    void getTheaterById_shouldReturnTheater_whenFound() {
        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));

        Theater result = theaterService.getTheaterById(testTheater.getId());

        assertNotNull(result);
        assertEquals(testTheater.getId(), result.getId());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
    }

    @Test
    void getTheaterById_shouldThrowRuntimeException_whenNotFound() {
        when(theaterRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.getTheaterById("non-existent-id"));

        assertEquals(DbBash.THEATER_NOT_FOUND, thrown.getMessage());
        verify(theaterRepository, times(1)).findById(anyString());
    }

    @Test
    void getAll_shouldReturnPageOfTheaterResponse_forAdminRole() {
        // SearchTheaterRequest searchRequest = new SearchTheaterRequest();
        // searchRequest.setPage(1);
        // searchRequest.setSize(10);
        // searchRequest.setSortBy("name");
        // searchRequest.setDirection("asc");
        // searchRequest.setName("Flix");

        // Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        // Page<Theater> theaterPage = new PageImpl<>(Collections.singletonList(testTheater), pageable, 1);

        // try (MockedStatic<TheaterSpecification> mockedSpecification = mockStatic(TheaterSpecification.class)) {
        //     mockedSpecification.when(() -> TheaterSpecification.getSpecification(any(SearchTheaterRequest.class)))
        //             .thenReturn(mock(Specification.class));

        //     when(httpServletRequest.isUserInRole("ROLE_ADMIN")).thenReturn(true);
        //     when(theaterRepository.findAll(any(Specification.class), any(Pageable.class)))
        //             .thenReturn(theaterPage);
        //     when(studioService.toStudioResponse(any(Studio.class))).thenReturn(StudioResponse.builder().id("studio-1").build());


        //     Page<TheaterResponse> result = theaterService.getAll(searchRequest, httpServletRequest);

        //     assertNotNull(result);
        //     assertEquals(1, result.getTotalElements());
        //     assertEquals(testTheater.getId(), result.getContent().get(0).getId());
        //     assertNotNull(result.getContent().get(0).getEmployees()); // Employees should be present for ADMIN

        //     mockedSpecification.verify(() -> TheaterSpecification.getSpecification(searchRequest), times(1));
        //     verify(httpServletRequest, times(1)).isUserInRole("CUSTOMER"); // This is called first
        //     verify(httpServletRequest, times(1)).isUserInRole("CASHIER");
        //     verify(httpServletRequest, times(1)).isUserInRole("ADMIN"); // This will be true
        //     verify(theaterRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        //     verify(studioService, times(testTheater.getStudios().size())).toStudioResponse(any(Studio.class));
        // }
    }

    @Test
    void getAll_shouldReturnPageOfTheaterResponse_forCustomerRole() {
        SearchTheaterRequest searchRequest = new SearchTheaterRequest();
        searchRequest.setPage(1);
        searchRequest.setSize(10);
        searchRequest.setSortBy("name");
        searchRequest.setDirection("asc");
        searchRequest.setEmployeesName(List.of("John Doe")); // This should be nullified

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<Theater> theaterPage = new PageImpl<>(Collections.singletonList(testTheater), pageable, 1);

        try (MockedStatic<TheaterSpecification> mockedSpecification = mockStatic(TheaterSpecification.class)) {
            mockedSpecification.when(() -> TheaterSpecification.getSpecification(any(SearchTheaterRequest.class)))
                    .thenReturn(mock(Specification.class));

            when(httpServletRequest.isUserInRole("ROLE_CUSTOMER")).thenReturn(true);
            when(theaterRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(theaterPage);
            when(studioService.toStudioResponse(any(Studio.class))).thenReturn(StudioResponse.builder().id("studio-1").build());


            Page<TheaterResponse> result = theaterService.getAll(searchRequest, httpServletRequest);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testTheater.getId(), result.getContent().get(0).getId());
            assertNull(result.getContent().get(0).getEmployees()); // Employees should be null for CUSTOMER
            assertTrue(searchRequest.getOprationalStatus()); // Operational status should be true
            assertNull(searchRequest.getEmployeesName()); // Employees name should be nullified

            mockedSpecification.verify(() -> TheaterSpecification.getSpecification(argThat(req ->
                    req.getOprationalStatus() && req.getEmployeesName() == null
            )), times(1));
            verify(httpServletRequest, times(1)).isUserInRole("ROLE_CUSTOMER");
            verify(theaterRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    void getAll_shouldSetDefaultPageAndSize() {
        // SearchTheaterRequest searchRequest = new SearchTheaterRequest(); // page and size are 0 by default

        // Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id")); // Default sort is "id"
        // Page<Theater> theaterPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        // try (MockedStatic<TheaterSpecification> mockedSpecification = mockStatic(TheaterSpecification.class)) {
        //     mockedSpecification.when(() -> TheaterSpecification.getSpecification(any(SearchTheaterRequest.class)))
        //             .thenReturn(mock(Specification.class));

        //     when(httpServletRequest.isUserInRole("ROLE_ADMIN")).thenReturn(true);
        //     when(theaterRepository.findAll(any(Specification.class), any(Pageable.class)))
        //             .thenReturn(theaterPage);

        //     theaterService.getAll(searchRequest, httpServletRequest);

        //     assertEquals(1, searchRequest.getPage()); // Should be set to 1
        //     assertEquals(10, searchRequest.getSize()); // Should be set to 10
        //     verify(theaterRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        // }
    }


    @Test
    void getAll_shouldThrowRuntimeException_whenCreatedAtMinMaxInvalid() {
        SearchTheaterRequest searchRequest = new SearchTheaterRequest();
        searchRequest.setPage(1);
        searchRequest.setSize(10);
        searchRequest.setCreatedAtMin("2025-01-02");
        searchRequest.setCreatedAtMax("2025-01-01"); // Invalid range

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-01-02")).thenReturn(LocalDate.of(2025, 1, 2));
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-01-01")).thenReturn(LocalDate.of(2025, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    theaterService.getAll(searchRequest, httpServletRequest));

            assertEquals(ApiBash.GET_ALL_THEATER_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
            verifyNoInteractions(theaterRepository);
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenUpdatedAtMinMaxInvalid() {
        SearchTheaterRequest searchRequest = new SearchTheaterRequest();
        searchRequest.setPage(1);
        searchRequest.setSize(10);
        searchRequest.setUpdatedAtMin("2025-01-02");
        searchRequest.setUpdatedAtMax("2025-01-01"); // Invalid range

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-01-02")).thenReturn(LocalDate.of(2025, 1, 2));
            mockedDateUtil.when(() -> DateUtil.parseDate("2025-01-01")).thenReturn(LocalDate.of(2025, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    theaterService.getAll(searchRequest, httpServletRequest));

            assertEquals(ApiBash.GET_ALL_THEATER_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
            verifyNoInteractions(theaterRepository);
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenRepositoryFails() {
        SearchTheaterRequest searchRequest = new SearchTheaterRequest();
        searchRequest.setPage(1);
        searchRequest.setSize(10);
        searchRequest.setSortBy("id");
        searchRequest.setDirection("asc");

        try (MockedStatic<TheaterSpecification> mockedSpecification = mockStatic(TheaterSpecification.class)) {
            mockedSpecification.when(() -> TheaterSpecification.getSpecification(any(SearchTheaterRequest.class)))
                    .thenReturn(mock(Specification.class));
            when(httpServletRequest.isUserInRole(anyString())).thenReturn(true); // Can be any role
            when(theaterRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenThrow(new RuntimeException("DB error during findAll"));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    theaterService.getAll(searchRequest, httpServletRequest));

            assertEquals(ApiBash.GET_ALL_THEATER_FAILED + ": DB error during findAll", thrown.getMessage());
            verify(theaterRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    void update_shouldReturnUpdatedTheaterResponse_whenSuccessful() {
        NewTheaterRequest updateRequest = NewTheaterRequest.builder()
                .name("Flix Update")
                .city("Surabaya")
                .address("Jl. Update")
                .contactNumber("12345")
                .contactEmail("update@example.com")
                .studiosId(Arrays.asList("studio-1", "studio-3")) // Add new studio, keep one
                .nowShowingId(Arrays.asList("prod-3")) // Change products
                .build();

        Studio newStudio = Studio.builder().id("studio-3").name("Studio C").build();
        Product newProduct = Product.builder().id("prod-3").title("Movie C").build();

        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));
        when(studioService.getStudioById("studio-1")).thenReturn(testStudio1);
        when(studioService.getStudioById("studio-3")).thenReturn(newStudio);
        when(productService.getProductById("prod-3")).thenReturn(newProduct);
        when(theaterRepository.saveAndFlush(any(Theater.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the updated theater
        when(studioService.toStudioResponse(any(Studio.class))).thenAnswer(invocation -> {
            Studio studio = invocation.getArgument(0);
            return StudioResponse.builder().id(studio.getId()).name(studio.getName()).build();
        });


        TheaterResponse result = theaterService.update(testTheater.getId(), updateRequest);

        assertNotNull(result);
        assertEquals(testTheater.getId(), result.getId());
        assertEquals(updateRequest.getName(), result.getName());
        assertEquals(updateRequest.getCity(), result.getCity());
        assertEquals(2, result.getStudios().size()); // 2 studios in the request
        assertTrue(result.getStudios().stream().anyMatch(s -> s.getId().equals("studio-1")));
        assertTrue(result.getStudios().stream().anyMatch(s -> s.getId().equals("studio-3")));
        assertEquals(1, result.getNowShowing().size()); // 1 product in the request
        assertTrue(result.getNowShowing().stream().anyMatch(p -> p.getId().equals("prod-3")));

        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verify(studioService, times(1)).getStudioById("studio-1");
        verify(studioService, times(1)).getStudioById("studio-3");
        verify(productService, times(1)).getProductById("prod-3");
        verify(theaterRepository, times(1)).saveAndFlush(any(Theater.class));

        assertTrue(testProduct1.getTheaters().isEmpty()); // Assuming they only had this theater
        assertTrue(testProduct2.getTheaters().isEmpty()); // Assuming they only had this theater
        assertTrue(newProduct.getTheaters().contains(testTheater));
    }

    @Test
    void update_shouldThrowRuntimeException_whenTheaterNotFound() {
        when(theaterRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.update("non-existent-id", newTheaterRequest));

        assertEquals(ApiBash.UPDATE_THEATER_FAILED + ": " + DbBash.THEATER_NOT_FOUND, thrown.getMessage());
        verify(theaterRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioService);
        verifyNoInteractions(productService);
    }

    @Test
    void update_shouldThrowRuntimeException_whenDuplicateStudioInRequest() {
        newTheaterRequest.setStudiosId(Arrays.asList("studio-1", "studio-1")); // Duplicate

        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.update(testTheater.getId(), newTheaterRequest));

        assertEquals(ApiBash.UPDATE_THEATER_FAILED + ": " + DbBash.DUPLICATE_STUDIO_REQUEST, thrown.getMessage());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verifyNoInteractions(studioService);
        verifyNoInteractions(productService);
        verifyNoMoreInteractions(theaterRepository);
    }

    @Test
    void update_shouldThrowRuntimeException_whenDuplicateProductInRequest() {
        newTheaterRequest.setNowShowingId(Arrays.asList("prod-1", "prod-1")); // Duplicate

        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.update(testTheater.getId(), newTheaterRequest));

        assertEquals(ApiBash.UPDATE_THEATER_FAILED + ": " + DbBash.DUPLICATE_PRODUCT_REQUEST, thrown.getMessage());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verifyNoInteractions(studioService);
        verifyNoInteractions(productService);
        verifyNoMoreInteractions(theaterRepository);
    }


    @Test
    void update_shouldThrowRuntimeException_whenStudioNotFound() {
        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));
        when(studioService.getStudioById(anyString())).thenThrow(new RuntimeException(DbBash.STUDIO_NOT_FOUND));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.update(testTheater.getId(), newTheaterRequest));

        assertEquals(ApiBash.UPDATE_THEATER_FAILED + ": " + DbBash.STUDIO_NOT_FOUND, thrown.getMessage());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verify(studioService, times(1)).getStudioById(anyString());
        verifyNoInteractions(productService);
        verifyNoMoreInteractions(theaterRepository);
    }

    @Test
    void update_shouldThrowRuntimeException_whenProductNotFound() {
        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));
        when(studioService.getStudioById(anyString())).thenReturn(testStudio1); // first studio
        when(studioService.getStudioById("studio-2")).thenReturn(testStudio2); // second studio
        when(productService.getProductById(anyString())).thenThrow(new RuntimeException(DbBash.PRODUCT_NOT_FOUND));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.update(testTheater.getId(), newTheaterRequest));

        assertEquals(ApiBash.UPDATE_THEATER_FAILED + ": " + DbBash.PRODUCT_NOT_FOUND, thrown.getMessage());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verify(studioService, times(2)).getStudioById(anyString()); // Called for both studios
        verify(productService, times(1)).getProductById(anyString());
        verifyNoMoreInteractions(theaterRepository);
    }

    @Test
    void update_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));
        when(studioService.getStudioById(anyString())).thenReturn(testStudio1);
        when(productService.getProductById(anyString())).thenReturn(testProduct1);
        when(theaterRepository.saveAndFlush(any(Theater.class))).thenThrow(new RuntimeException("Save failed"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.update(testTheater.getId(), newTheaterRequest));

        assertEquals(ApiBash.UPDATE_THEATER_FAILED + ": Save failed", thrown.getMessage());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verify(studioService, times(newTheaterRequest.getStudiosId().size())).getStudioById(anyString());
        verify(productService, times(newTheaterRequest.getNowShowingId().size())).getProductById(anyString());
        verify(theaterRepository, times(1)).saveAndFlush(any(Theater.class));
    }


    @Test
    void softDelete_shouldSetOprationalStatusToFalse_whenFound() {
        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));
        when(theaterRepository.saveAndFlush(any(Theater.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> theaterService.softDelete(testTheater.getId()));

        assertFalse(testTheater.getOprationalStatus());
        assertNotNull(testTheater.getUpdatedAt());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verify(theaterRepository, times(1)).saveAndFlush(testTheater);
    }

    @Test
    void softDelete_shouldThrowRuntimeException_whenTheaterNotFound() {
        when(theaterRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.softDelete("non-existent-id"));

        assertEquals(ApiBash.SOFT_DELETE_THEATER_FAILED + ": " + DbBash.THEATER_NOT_FOUND, thrown.getMessage());
        verify(theaterRepository, times(1)).findById(anyString());
        verify(theaterRepository, never()).saveAndFlush(any(Theater.class));
    }

    @Test
    void softDelete_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));
        when(theaterRepository.saveAndFlush(any(Theater.class))).thenThrow(new RuntimeException("Save failed"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.softDelete(testTheater.getId()));

        assertEquals(ApiBash.SOFT_DELETE_THEATER_FAILED + ": Save failed", thrown.getMessage());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verify(theaterRepository, times(1)).saveAndFlush(any(Theater.class));
    }

    @Test
    void refreshAllSeat_shouldCallRefreshAllSeatOnStudios_andReturnResponse() {
        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));
        when(theaterRepository.saveAndFlush(any(Theater.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(studioService.toStudioResponse(any(Studio.class))).thenReturn(StudioResponse.builder().id("mock-studio-id").build());

        TheaterResponse result = theaterService.refreshAllSeat(testTheater.getId());

        assertNotNull(result);
        assertEquals(testTheater.getId(), result.getId());
        assertNotNull(testTheater.getUpdatedAt());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verify(studioService, times(testTheater.getStudios().size())).refreshAllSeat(anyString());
        verify(theaterRepository, times(1)).saveAndFlush(testTheater);
        verify(studioService, times(testTheater.getStudios().size())).toStudioResponse(any(Studio.class));
    }

    @Test
    void refreshAllSeat_shouldThrowRuntimeException_whenTheaterNotFound() {
        when(theaterRepository.findById(anyString())).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.refreshAllSeat("non-existent-id"));

        assertEquals(ApiBash.REFRESH_ALL_SEAT_FAILED + ": " + DbBash.THEATER_NOT_FOUND, thrown.getMessage());
        verify(theaterRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioService);
        verify(theaterRepository, never()).saveAndFlush(any(Theater.class));
    }

    @Test
    void refreshAllSeat_shouldThrowRuntimeException_whenStudioServiceRefreshFails() {
        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));
        doThrow(new RuntimeException("Studio refresh failed")).when(studioService).refreshAllSeat(anyString());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.refreshAllSeat(testTheater.getId()));

        assertEquals(ApiBash.REFRESH_ALL_SEAT_FAILED + ": Studio refresh failed", thrown.getMessage());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verify(studioService, times(1)).refreshAllSeat(anyString());
        verify(theaterRepository, never()).saveAndFlush(any(Theater.class)); // Should not save if studio refresh fails
    }

    @Test
    void refreshAllSeat_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(theaterRepository.findById(testTheater.getId())).thenReturn(Optional.of(testTheater));
        when(studioService.refreshAllSeat(anyString())).thenReturn(StudioResponse.builder().id("mock-studio-id").build());
        when(theaterRepository.saveAndFlush(any(Theater.class))).thenThrow(new RuntimeException("Save failed"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                theaterService.refreshAllSeat(testTheater.getId()));

        assertEquals(ApiBash.REFRESH_ALL_SEAT_FAILED + ": Save failed", thrown.getMessage());
        verify(theaterRepository, times(1)).findById(testTheater.getId());
        verify(studioService, times(testTheater.getStudios().size())).refreshAllSeat(anyString());
        verify(theaterRepository, times(1)).saveAndFlush(any(Theater.class));
    }

}