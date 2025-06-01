package com.flix.flix.service.impl;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ESchedule;
import com.flix.flix.constant.custom_enum.ESeat;
import com.flix.flix.constant.custom_enum.EStudioSize;
import com.flix.flix.entity.*;
import com.flix.flix.model.request.NewProductPricingRequest;
import com.flix.flix.model.request.NewProductSchedulingRequest;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.request.NewStudioSeatScheduleRequest;
import com.flix.flix.model.request.search.SearchStudioRequest;
import com.flix.flix.model.response.ProductPricingResponse;
import com.flix.flix.model.response.ProductSchedulingResponse;
import com.flix.flix.model.response.StudioResponse;
import com.flix.flix.model.response.StudioSeatScheduleResponse;
import com.flix.flix.repository.StudioRepository;
import com.flix.flix.repository.TheaterRepository;
import com.flix.flix.service.ProductPricingService;
import com.flix.flix.service.ProductSchedulingService;
import com.flix.flix.service.ProductService;
import com.flix.flix.service.StudioSeatScheduleService;
import com.flix.flix.specification.StudioSpecification;
import com.flix.flix.util.TimeUtil;
import com.flix.flix.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudioServiceImplTest {

    @Mock
    private StudioRepository studioRepository;
    @Mock
    private ProductPricingService productPricingService;
    @Mock
    private ProductSchedulingService productSchedulingService;
    @Mock
    private ProductService productService;
    @Mock
    private StudioSeatScheduleService studioSeatScheduleService;
    @Mock
    private TheaterRepository theaterRepository;
    @Mock
    private TokenUtil tokenUtil;
    @Mock
    private HttpServletRequest httpServletRequest;

    @Spy
    @InjectMocks
    private StudioServiceImpl studioService;

    private Theater theater;
    private Studio studio;
    private NewStudioRequest newStudioRequest;
    private Product product1;
    private Product product2;
    private ProductPricing productPricing1;
    private ProductPricing productPricing2;
    private ProductScheduling productScheduling1;
    private ProductScheduling productScheduling2;
    private StudioSeatSchedule studioSeatSchedule1;
    private StudioSeatSchedule studioSeatSchedule2;
    private AppUser appUser;
    private Employee employee;

    @BeforeEach
    void setUp() {
        theater = Theater.builder()
                .id("t1")
                .name("Theater A")
                .products(new ArrayList<>())
                .build();

        product1 = Product.builder().id("p1").title("Movie 1").duration(120L).build();
        product2 = Product.builder().id("p2").title("Movie 2").duration(90L).build();
        theater.getProducts().add(product1);
        theater.getProducts().add(product2);


        studio = Studio.builder()
                .id("s1")
                .name("Studio 1")
                .studioSize(EStudioSize.STUDIO_REGULER_LARGE)
                .seatLayout(Arrays.asList(ESeat.SEAT_A1, ESeat.SEAT_A2))
                .isActive(true)
                .theater(theater)
                .productPricing(new ArrayList<>())
                .productScheduling(new ArrayList<>())
                .studioSeatSchedule(new ArrayList<>())
                .build();

        productPricing1 = ProductPricing.builder().id("pp1").productIdPricing(product1).build();
        productPricing2 = ProductPricing.builder().id("pp2").productIdPricing(product2).build();

        productScheduling1 = ProductScheduling.builder().id("ps1").productIdScheduling(product1).schedule(ESchedule.SCHEDULE_9_00).build();
        productScheduling2 = ProductScheduling.builder().id("ps2").productIdScheduling(product2).schedule(ESchedule.SCHEDULE_10_00).build();

        studioSeatSchedule1 = StudioSeatSchedule.builder().id("sss1").studio(studio).productScheduling(productScheduling1).availableSeat(new ArrayList<>()).bookedSeat(new ArrayList<>()).build();
        studioSeatSchedule2 = StudioSeatSchedule.builder().id("sss2").studio(studio).productScheduling(productScheduling2).availableSeat(new ArrayList<>()).bookedSeat(new ArrayList<>()).build();

        studio.getProductPricing().add(productPricing1);
        studio.getProductScheduling().add(productScheduling1);
        studio.getStudioSeatSchedule().add(studioSeatSchedule1);


        newStudioRequest = NewStudioRequest.builder()
                .name("New Studio 1")
                .studioSize("LARGE")
                .seatLayout(Arrays.asList("A1", "A2", "B1", "B2"))
                .theaterId("t1")
                .productPricingRequests(Arrays.asList(
                        NewProductPricingRequest.builder().productId("p1").weekdayPrice(100000.0).build(),
                        NewProductPricingRequest.builder().productId("p2").weekdayPrice(80000.0).build()
                ))
                .productSchedulingRequests(Arrays.asList(
                        NewProductSchedulingRequest.builder().productId("p1").schedule("10:00").build(),
                        NewProductSchedulingRequest.builder().productId("p2").schedule("12:30").build()
                ))
                .studioSeatScheduleRequests(Arrays.asList(
                        NewStudioSeatScheduleRequest.builder().studioId("s1").productSchedulingId("ps1").build(),
                        NewStudioSeatScheduleRequest.builder().studioId("s1").productSchedulingId("ps2").build()
                ))
                .build();

        employee = Employee.builder().id("emp1").theater(theater).build();
        appUser = AppUser.builder().id("user1").employee(employee).build();
    }

    @Test
    void testCreateStudio_Success() {
        NewStudioRequest request = new NewStudioRequest();
        request.setName("Studio 1");
        request.setStudioSize("Reguler Small");
        request.setSeatLayout(List.of("A1", "A2"));
        request.setTheaterId("theater-1");

        Theater theater = Theater.builder().id("theater-1").build();

        when(theaterRepository.findById("theater-1")).thenReturn(Optional.of(theater));
        when(studioRepository.saveAndFlush(any(Studio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudioResponse response = studioService.create(request);

        assertEquals("Studio 1", response.getName());
        verify(studioRepository).saveAndFlush(any(Studio.class));
    }

    @Test
    void testCreateStudio_TheaterNotFound_ThrowsException() {
        NewStudioRequest request = new NewStudioRequest();
        request.setTheaterId("invalid-theater");

        when(theaterRepository.findById("invalid-theater")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studioService.create(request));
        assertTrue(exception.getMessage().contains(DbBash.THEATER_NOT_FOUND));
    }

    @Test
    void testCreateStudio_InvalidSeatLayout_ThrowsException() {
        NewStudioRequest request = new NewStudioRequest();
        request.setTheaterId("theater-1");
        request.setStudioSize("REGULER SMALL");
        request.setSeatLayout(List.of("INVALID_SEAT"));

        when(theaterRepository.findById("theater-1")).thenReturn(Optional.of(new Theater()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studioService.create(request));
        assertTrue(exception.getMessage().contains(ApiBash.CREATE_STUDIO_FAILED));
    }

    @Test
    void getAll_Success_WithDefaultPagination() {
        SearchStudioRequest searchRequest = new SearchStudioRequest(); // Defaults page to 1, size to 10
        Page<Studio> studioPage = new PageImpl<>(Collections.singletonList(studio));
        when(studioRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(studioPage);

        Page<StudioResponse> response = studioService.getAll(searchRequest);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("s1", response.getContent().get(0).getId());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(studioRepository).findAll(any(Specification.class), pageableCaptor.capture());
        assertEquals(0, pageableCaptor.getValue().getPageNumber()); // 1 -> 0
        assertEquals(10, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void getAll_Success_WithCustomPaginationAndSorting() {
        SearchStudioRequest searchRequest = new SearchStudioRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setSortBy("name");
        searchRequest.setDirection("DESC");

        Page<Studio> studioPage = new PageImpl<>(Collections.singletonList(studio));
        when(studioRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(studioPage);

        Page<StudioResponse> response = studioService.getAll(searchRequest);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(studioRepository).findAll(any(Specification.class), pageableCaptor.capture());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(10, pageableCaptor.getValue().getPageSize());
        assertEquals("name: DESC", pageableCaptor.getValue().getSort().toString());
    }

    @Test
    void getAllActive_Success() {
        SearchStudioRequest searchRequest = new SearchStudioRequest();
        Page<Studio> studioPage = new PageImpl<>(Collections.singletonList(studio));
        when(studioRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(studioPage);

        Page<StudioResponse> response = studioService.getAllActive(searchRequest);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertTrue(searchRequest.getIsActive()); // Verify isActive is set to true
    }

    @Test
    void getAllActive_ThrowsRuntimeException_WhenServiceFails() {
        when(studioRepository.findAll(any(Specification.class), any(Pageable.class))).thenThrow(new RuntimeException("DB Error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> studioService.getAllActive(new SearchStudioRequest()));
        assertTrue(thrown.getMessage().contains(ApiBash.GET_ALL_STUDIO_FAILED));
        assertTrue(thrown.getMessage().contains("DB Error"));
    }

    @Test
    void getById_Success() {
        when(studioRepository.findById("s1")).thenReturn(Optional.of(studio));

        StudioResponse response = studioService.getById("s1");
        assertNotNull(response);
        assertEquals("s1", response.getId());
        assertEquals("Studio 1", response.getName());
    }

    @Test
    void getById_ThrowsException_OnNotFound() {
        when(studioRepository.findById("nonExistentId")).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> studioService.getById("nonExistentId"));
        assertTrue(thrown.getMessage().contains(ApiBash.GET_STUDIO_FAILED));
        assertTrue(thrown.getMessage().contains(DbBash.STUDIO_NOT_FOUND));
    }

    @Test
    void getStudioById_Success() {
        when(studioRepository.findById("s1")).thenReturn(Optional.of(studio));
        Studio result = studioService.getStudioById("s1");
        assertNotNull(result);
        assertEquals("s1", result.getId());
    }

    @Test
    void getStudioById_ThrowsException_OnNotFound() {
        when(studioRepository.findById("nonExistentId")).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> studioService.getStudioById("nonExistentId"));
        assertTrue(thrown.getMessage().contains(DbBash.STUDIO_NOT_FOUND));
    }

    @Test
    void testUpdateStudio_Success() {
        String studioId = "studio-1";

        NewStudioRequest request = new NewStudioRequest();
        request.setName("Studio Baru");
        request.setStudioSize("REGULER SMALL");
        request.setStudioSeatScheduleRequests(new ArrayList<>());

        Studio studio = Studio.builder()
            .id(studioId)
            .seatLayout(List.of(ESeat.SEAT_A1, ESeat.SEAT_A2))
            .build();

        when(studioRepository.findById(studioId)).thenReturn(Optional.of(studio));
        doReturn(studio).when(studioRepository).saveAndFlush(any());

        doReturn(Set.of("prod-1")).when(studioService).validateProductPricingRequest(any());
        doReturn(Set.of("prod-1")).when(studioService).validateProductSchedulingRequest(any());
        doNothing().when(studioService).validateProductSchedulingRequestSchedule(any());
        doNothing().when(studioService).validateProductPricingAndScheduling(any(), any());
        doNothing().when(studioService).validateAvailabilityProduct(any(), any());
        doReturn(new ArrayList<>()).when(studioService).getNewProductPricings(any(), any());
        doReturn(new ArrayList<>()).when(studioService).getNewProductSchedulings(any(), any());

        StudioResponse response = studioService.update(studioId, request);

        assertEquals("Studio Baru", response.getName());
        verify(studioRepository).saveAndFlush(any(Studio.class));
    }

    @Test
    void testUpdateStudio_StudioIdMismatch_ThrowsException() {
        String studioId = "studio-1";

        NewStudioSeatScheduleRequest seatScheduleRequest = new NewStudioSeatScheduleRequest();
        seatScheduleRequest.setStudioId("different-id");
        seatScheduleRequest.setProductSchedulingId("sched-1");

        NewStudioRequest request = new NewStudioRequest();
        request.setName("Studio Baru");
        request.setStudioSize("REGULER SMALL");
        request.setStudioSeatScheduleRequests(List.of(seatScheduleRequest));

        Studio studio = Studio.builder()
            .id(studioId)
            .seatLayout(List.of(ESeat.SEAT_A1, ESeat.SEAT_A2))
            .build();

        when(studioRepository.findById(studioId)).thenReturn(Optional.of(studio));
        when(studioSeatScheduleService.getStudioSeatScheduleByAttribute(any(), any())).thenReturn(null);

        doReturn(Set.of("prod-1")).when(studioService).validateProductPricingRequest(any());
        doReturn(Set.of("prod-1")).when(studioService).validateProductSchedulingRequest(any());
        doNothing().when(studioService).validateProductSchedulingRequestSchedule(any());
        doNothing().when(studioService).validateProductPricingAndScheduling(any(), any());
        doNothing().when(studioService).validateAvailabilityProduct(any(), any());
        doReturn(new ArrayList<>()).when(studioService).getNewProductPricings(any(), any());
        doReturn(new ArrayList<>()).when(studioService).getNewProductSchedulings(any(), any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studioService.update(studioId, request));
        assertTrue(exception.getMessage().contains(DbBash.STUDIO_ID_NOT_MATCH));
    }
    @Test
    void softDelete_Success() {
        when(studioRepository.findById("s1")).thenReturn(Optional.of(studio));
        when(studioRepository.saveAndFlush(any(Studio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> studioService.softDelete("s1"));

        ArgumentCaptor<Studio> studioCaptor = ArgumentCaptor.forClass(Studio.class);
        verify(studioRepository, times(1)).saveAndFlush(studioCaptor.capture());

        Studio capturedStudio = studioCaptor.getValue();
        assertEquals("s1", capturedStudio.getId());
        assertFalse(capturedStudio.getIsActive());
    }

    @Test
    void softDelete_ThrowsException_WhenStudioNotFound() {
        when(studioRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> studioService.softDelete("nonExistentId"));
        assertTrue(thrown.getMessage().contains(ApiBash.SOFT_DELETE_STUDIO_FAILED));
        assertTrue(thrown.getMessage().contains(DbBash.STUDIO_NOT_FOUND));
    }

    @Test
    void getByTheaterId_Success() {
        SearchStudioRequest searchRequest = new SearchStudioRequest();
        searchRequest.setTheaterId("t1");
        Page<Studio> studioPage = new PageImpl<>(Collections.singletonList(studio));
        when(studioRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(studioPage);

        Page<StudioResponse> response = studioService.getByTheaterId("t1");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("s1", response.getContent().get(0).getId());

        ArgumentCaptor<SearchStudioRequest> searchRequestCaptor = ArgumentCaptor.forClass(SearchStudioRequest.class);
        verify(studioService, times(1)).getAllActive(searchRequestCaptor.capture());
        assertEquals("t1", searchRequestCaptor.getValue().getTheaterId());
        assertTrue(searchRequestCaptor.getValue().getIsActive());
    }

    @Test
    void testGetByProductIdAndTheaterId_ReturnsFilteredStudios() {
        String theaterId = "theater-1";
        String productId = "product-1";

        ProductPricingResponse pricing1 = ProductPricingResponse.builder().productId(productId).build();
        ProductPricingResponse pricing2 = ProductPricingResponse.builder().productId("product-2").build();

        ProductSchedulingResponse scheduling1 = ProductSchedulingResponse.builder().productId(productId).build();
        ProductSchedulingResponse scheduling2 = ProductSchedulingResponse.builder().productId("product-2").build();

        StudioSeatScheduleResponse seatSchedule1 = StudioSeatScheduleResponse.builder()
            .productScheduling(ProductSchedulingResponse.builder().productId(productId).build())
            .build();
        StudioSeatScheduleResponse seatSchedule2 = StudioSeatScheduleResponse.builder()
            .productScheduling(ProductSchedulingResponse.builder().productId("product-2").build())
            .build();

        StudioResponse studio1 = StudioResponse.builder()
            .id("studio-1")
            .productPricing(new ArrayList<>(List.of(pricing1, pricing2)))
            .productScheduling(new ArrayList<>(List.of(scheduling1, scheduling2)))
            .studioSeatSchedule(new ArrayList<>(List.of(seatSchedule1, seatSchedule2)))
            .build();

        doReturn(new PageImpl<>(List.of(studio1)))
            .when(studioService)
            .getAllActive(any());

        Page<StudioResponse> result = studioService.getByProductIdAndTheaterId(theaterId, productId);

        StudioResponse filteredStudio = result.getContent().get(0);
        assertEquals(1, filteredStudio.getProductPricing().size());
        assertEquals(productId, filteredStudio.getProductPricing().get(0).getProductId());

        assertEquals(1, filteredStudio.getProductScheduling().size());
        assertEquals(productId, filteredStudio.getProductScheduling().get(0).getProductId());

        assertEquals(1, filteredStudio.getStudioSeatSchedule().size());
        assertEquals(productId, filteredStudio.getStudioSeatSchedule().get(0).getProductScheduling().getProductId());

        ArgumentCaptor<SearchStudioRequest> captor = ArgumentCaptor.forClass(SearchStudioRequest.class);
        verify(studioService).getAllActive(captor.capture());
        SearchStudioRequest captured = captor.getValue();
        assertEquals(theaterId, captured.getTheaterId());
        assertEquals(productId, captured.getProductId());
    }


    @Test
    void getByCredentials_Success() {
        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenReturn(appUser);
        Page<Studio> studioPage = new PageImpl<>(Collections.singletonList(studio));
        when(studioRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(studioPage);

        Page<StudioResponse> response = studioService.getByCredentials(httpServletRequest);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("s1", response.getContent().get(0).getId());

        verify(tokenUtil, times(1)).getAppUserByToken(httpServletRequest);
        ArgumentCaptor<SearchStudioRequest> searchRequestCaptor = ArgumentCaptor.forClass(SearchStudioRequest.class);
        verify(studioService, times(1)).getAllActive(searchRequestCaptor.capture());
        assertEquals("t1", searchRequestCaptor.getValue().getTheaterId());
        assertTrue(searchRequestCaptor.getValue().getIsActive());
    }

    @Test
    void getByCredentials_ThrowsException_WhenTokenUtilFails() {
        when(tokenUtil.getAppUserByToken(httpServletRequest)).thenThrow(new RuntimeException("Invalid Token"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> studioService.getByCredentials(httpServletRequest));
        assertTrue(thrown.getMessage().contains("Invalid Token"));
    }

    @Test
    void refreshAllSeat_Success_WithinAllowedTime() {
        if (LocalDateTime.now().getHour() > 8 && LocalDateTime.now().getHour() < 22){
            assertThrows(RuntimeException.class, () -> studioService.refreshAllSeat(anyString()));
        } else {
            when(studioRepository.findAll(any(Specification.class))).thenReturn(List.of(studio));
            when(studioRepository.saveAndFlush(any(Studio.class))).thenAnswer(invocation -> invocation.getArgument(0));

            assertDoesNotThrow(() -> studioService.refreshAllSeat(anyString()));
            verify(studioRepository, times(1)).findAll(any(Specification.class));
        }
    }

    @Test
    void validateAvailabilityProduct_shouldThrowException_whenProductNotInTheater() {
        Studio studio = mock(Studio.class);
        Theater theater = mock(Theater.class);
        Product product = mock(Product.class);
        when(studio.getTheater()).thenReturn(theater);
        when(productService.getProductById("product1")).thenReturn(product);
        when(theater.getProducts()).thenReturn(List.of()); // kosong = error

        NewStudioRequest request = new NewStudioRequest();
        request.setProductPricingRequests(List.of(NewProductPricingRequest.builder().productId("product1").build()));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studioService.validateAvailabilityProduct(request, studio));
        assertEquals(DbBash.PRODUCT_IS_NOT_SHOWING_IN_THEATER, ex.getMessage());
    }

    @Test
    void validateProductPricingRequest_shouldThrowException_whenDuplicateProductIds() {
        NewStudioRequest request = new NewStudioRequest();
        request.setProductPricingRequests(List.of(
            NewProductPricingRequest.builder().productId("product1").build(),
            NewProductPricingRequest.builder().productId("product1").build()
        ));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studioService.validateProductPricingRequest(request));
        assertEquals(DbBash.PRODUCT_ALREADY_PRICED, ex.getMessage());
    }

    @Test
    void validateProductSchedulingRequest_shouldThrowException_whenDuplicateSchedules() {
        NewStudioRequest request = new NewStudioRequest();
        request.setProductSchedulingRequests(List.of(
            new NewProductSchedulingRequest("product1", "10:00"),
            new NewProductSchedulingRequest("product1", "10:00")
        ));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studioService.validateProductSchedulingRequest(request));
        assertEquals(DbBash.PRODUCT_ALREADY_SCHEDULED, ex.getMessage());
    }

    @Test
    void validateProductSchedulingRequestSchedule_shouldThrowException_whenScheduleConflict() {
        Product product1 = new Product(); product1.setId("p1"); product1.setDuration(60L);
        Product product2 = new Product(); product2.setId("p2"); product2.setDuration(90L);
        
        when(productService.getProductById("p1")).thenReturn(product1);
        when(productService.getProductById("p2")).thenReturn(product2);

        NewStudioRequest request = new NewStudioRequest();
        request.setProductSchedulingRequests(List.of(
            NewProductSchedulingRequest.builder().productId("p1").schedule("11:00").build(),
            NewProductSchedulingRequest.builder().productId("p2").schedule("10:30").build() // 10:30 < 11:00
        ));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studioService.validateProductSchedulingRequestSchedule(request));
        assertEquals(DbBash.SCHEDULE_CONFLICT, ex.getMessage());
    }

    @Test
    void validateProductPricingAndScheduling_shouldThrowException_whenMismatch() {
        Set<String> pricing = new HashSet<>(List.of("p1", "p2"));
        Set<String> scheduling = new HashSet<>(List.of("p1")); // mismatch

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studioService.validateProductPricingAndScheduling(pricing, scheduling));
        assertEquals(DbBash.PRODUCT_PRICING_AND_PRODUCT_SCHEDULING_NOT_MATCH, ex.getMessage());
    }

    @Test
    void getNewProductPricings_shouldCreateAndAssignProductPricing() {
        Studio studio = new Studio(); studio.setProductPricing(new ArrayList<>());
        NewProductPricingRequest req = NewProductPricingRequest.builder().productId("p1").build();
        NewStudioRequest studioRequest = new NewStudioRequest();
        studioRequest.setProductPricingRequests(List.of(req));

        ProductPricing pricing = new ProductPricing(); pricing.setStudios(new ArrayList<>());
        when(productPricingService.getProductPricingByAttribute(req)).thenReturn(null);
        when(productPricingService.create(req)).thenReturn(pricing);

        List<ProductPricing> result = studioService.getNewProductPricings(studioRequest, studio);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getStudios().contains(studio));
    }

    @Test
    void getNewProductSchedulings_shouldCreateAndAssignProductScheduling() {
        Studio studio = new Studio(); studio.setProductScheduling(new ArrayList<>());
        NewProductSchedulingRequest req = NewProductSchedulingRequest.builder().productId("p1").schedule("10:00").build();
        NewStudioRequest studioRequest = new NewStudioRequest();
        studioRequest.setProductSchedulingRequests(List.of(req));

        ProductScheduling scheduling = new ProductScheduling(); scheduling.setStudios(new ArrayList<>());
        when(productSchedulingService.getProductSchedulingByAttribute(req)).thenReturn(null);
        when(productSchedulingService.create(req)).thenReturn(scheduling);

        List<ProductScheduling> result = studioService.getNewProductSchedulings(studioRequest, studio);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getStudios().contains(studio));
    }


}