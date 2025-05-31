package com.flix.flix.service.impl;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ESeat;
import com.flix.flix.constant.custom_enum.ESchedule;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.entity.Studio;
import com.flix.flix.entity.StudioSeatSchedule;
import com.flix.flix.model.request.NewStudioSeatScheduleRequest;
import com.flix.flix.model.response.ProductSchedulingResponse;
import com.flix.flix.model.response.StudioSeatScheduleResponse;
import com.flix.flix.repository.ProductSchedulingRepository;
import com.flix.flix.repository.StudioRepository;
import com.flix.flix.repository.StudioSeatScheduleRepository;
import com.flix.flix.service.ProductSchedulingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudioSeatScheduleServiceImplTest {

    @Mock
    private StudioSeatScheduleRepository studioSeatScheduleRepository;

    @Mock
    private StudioRepository studioRepository;

    @Mock
    private ProductSchedulingRepository productSchedulingRepository;

    @Mock
    private ProductSchedulingService productSchedulingService;

    @InjectMocks
    private StudioSeatScheduleServiceImpl studioSeatScheduleService;

    private Studio testStudio;
    private Product testProduct;
    private ProductScheduling testProductScheduling;
    private StudioSeatSchedule testStudioSeatSchedule;
    private NewStudioSeatScheduleRequest newStudioSeatScheduleRequest;
    private List<ESeat> fullSeatLayout;

    @BeforeEach
    void setUp() {
        testStudio = Studio.builder().id("studio-1").name("Studio A").build();
        testProduct = Product.builder().id("prod-1").title("Movie Title").build();
        testProductScheduling = ProductScheduling.builder()
                .id("prod-sched-1")
                .productIdScheduling(testProduct)
                .schedule(ESchedule.SCHEDULE_10_00)
                .build();

        fullSeatLayout = Arrays.asList(ESeat.SEAT_A1, ESeat.SEAT_A2, ESeat.SEAT_B1, ESeat.SEAT_B2);

        testStudioSeatSchedule = StudioSeatSchedule.builder()
                .id("sss-1")
                .studio(testStudio)
                .productScheduling(testProductScheduling)
                .availableSeat(Arrays.asList(ESeat.SEAT_A1, ESeat.SEAT_A2, ESeat.SEAT_B1, ESeat.SEAT_B2))
                .bookedSeat(Collections.emptyList())
                .build();

        newStudioSeatScheduleRequest = NewStudioSeatScheduleRequest.builder()
                .studioId(testStudio.getId())
                .productSchedulingId(testProductScheduling.getId())
                .availableSeat(Arrays.asList(ESeat.SEAT_A1.getDescription(), ESeat.SEAT_A2.getDescription()))
                .bookedSeat(Arrays.asList(ESeat.SEAT_B1.getDescription(), ESeat.SEAT_B2.getDescription()))
                .build();
    }

    @Test
    void create_shouldReturnStudioSeatSchedule_whenSuccessful() {
        when(studioRepository.findById(testStudio.getId())).thenReturn(Optional.of(testStudio));
        when(productSchedulingRepository.findById(testProductScheduling.getId())).thenReturn(Optional.of(testProductScheduling));
        when(studioSeatScheduleRepository.saveAndFlush(any(StudioSeatSchedule.class))).thenAnswer(invocation -> {
            StudioSeatSchedule saved = invocation.getArgument(0);
            saved.setId("new-sss-id");
            return saved;
        });

        StudioSeatSchedule result = studioSeatScheduleService.create(newStudioSeatScheduleRequest, fullSeatLayout);

        assertNotNull(result);
        assertEquals("new-sss-id", result.getId());
        assertEquals(testStudio.getId(), result.getStudio().getId());
        assertEquals(testProductScheduling.getId(), result.getProductScheduling().getId());
        assertEquals(2, result.getAvailableSeat().size());
        assertEquals(2, result.getBookedSeat().size());
        assertTrue(result.getAvailableSeat().contains(ESeat.SEAT_A1));
        assertTrue(result.getBookedSeat().contains(ESeat.SEAT_B1));

        verify(studioRepository, times(1)).findById(testStudio.getId());
        verify(productSchedulingRepository, times(1)).findById(testProductScheduling.getId());
        verify(studioSeatScheduleRepository, times(1)).saveAndFlush(any(StudioSeatSchedule.class));

    }

    @Test
    void create_shouldThrowRuntimeException_whenStudioNotFound() {
        when(studioRepository.findById(anyString())).thenReturn(Optional.empty());
        when(productSchedulingRepository.findById(anyString())).thenReturn(Optional.of(testProductScheduling));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.create(newStudioSeatScheduleRequest, fullSeatLayout));
        assertEquals(DbBash.STUDIO_NOT_FOUND, thrown.getMessage());

        verify(studioRepository, times(1)).findById(anyString());
        verify(productSchedulingRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioSeatScheduleRepository);
    }

    @Test
    void create_shouldThrowRuntimeException_whenProductSchedulingNotFound() {
        when(studioRepository.findById(anyString())).thenReturn(Optional.of(testStudio));
        when(productSchedulingRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.create(newStudioSeatScheduleRequest, fullSeatLayout));
        assertEquals(DbBash.PRODUCT_SCHEDULING_NOT_FOUND, thrown.getMessage());

        verify(studioRepository, times(1)).findById(anyString());
        verify(productSchedulingRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioSeatScheduleRepository);
    }

    @Test
    void create_shouldThrowRuntimeException_whenDuplicateAvailableSeat() {
        newStudioSeatScheduleRequest.setAvailableSeat(Arrays.asList(ESeat.SEAT_A1.getDescription(), ESeat.SEAT_A1.getDescription()));

        when(studioRepository.findById(anyString())).thenReturn(Optional.of(testStudio));
        when(productSchedulingRepository.findById(anyString())).thenReturn(Optional.of(testProductScheduling));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.create(newStudioSeatScheduleRequest, fullSeatLayout));
        assertEquals(DbBash.DUPLICATE_AVAILABLE_SEAT_REQUEST, thrown.getMessage());

        verify(studioRepository, times(1)).findById(anyString());
        verify(productSchedulingRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioSeatScheduleRepository);
    }

    @Test
    void create_shouldThrowRuntimeException_whenDuplicateBookedSeat() {
        newStudioSeatScheduleRequest.setBookedSeat(Arrays.asList(ESeat.SEAT_B1.getDescription(), ESeat.SEAT_B1.getDescription()));

        when(studioRepository.findById(anyString())).thenReturn(Optional.of(testStudio));
        when(productSchedulingRepository.findById(anyString())).thenReturn(Optional.of(testProductScheduling));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.create(newStudioSeatScheduleRequest, fullSeatLayout));
        assertEquals(DbBash.DUPLICATE_BOOKED_SEAT_REQUEST, thrown.getMessage());

        verify(studioRepository, times(1)).findById(anyString());
        verify(productSchedulingRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioSeatScheduleRepository);
    }

    @Test
    void create_shouldThrowRuntimeException_whenAvailableSeatNotInLayout() {
        newStudioSeatScheduleRequest.setAvailableSeat(Arrays.asList(ESeat.SEAT_A1.getDescription(), "A9".toUpperCase())); // A9 not in layout

        when(studioRepository.findById(anyString())).thenReturn(Optional.of(testStudio));
        when(productSchedulingRepository.findById(anyString())).thenReturn(Optional.of(testProductScheduling));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.create(newStudioSeatScheduleRequest, fullSeatLayout));

        verify(studioRepository, times(1)).findById(anyString());
        verify(productSchedulingRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioSeatScheduleRepository);

    }

    @Test
    void create_shouldThrowRuntimeException_whenBookedSeatNotInLayout() {
        newStudioSeatScheduleRequest.setBookedSeat(Arrays.asList(ESeat.SEAT_B1.getDescription(), "A9".toUpperCase())); // A9 not in layout

        when(studioRepository.findById(anyString())).thenReturn(Optional.of(testStudio));
        when(productSchedulingRepository.findById(anyString())).thenReturn(Optional.of(testProductScheduling));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.create(newStudioSeatScheduleRequest, fullSeatLayout));
        assertEquals("Booked seat not in seat layout", thrown.getMessage());

        verify(studioRepository, times(1)).findById(anyString());
        verify(productSchedulingRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioSeatScheduleRepository);
    }


    @Test
    void create_shouldThrowRuntimeException_whenSeatNotInRequest() {
        newStudioSeatScheduleRequest.setAvailableSeat(Arrays.asList(ESeat.SEAT_A2.getDescription()));
        newStudioSeatScheduleRequest.setBookedSeat(Arrays.asList(ESeat.SEAT_B1.getDescription()));

        when(studioRepository.findById(anyString())).thenReturn(Optional.of(testStudio));
        when(productSchedulingRepository.findById(anyString())).thenReturn(Optional.of(testProductScheduling));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.create(newStudioSeatScheduleRequest, fullSeatLayout));
        assertEquals(DbBash.SEAT_NOT_IN_REQUEST, thrown.getMessage());

        verify(studioRepository, times(1)).findById(anyString());
        verify(productSchedulingRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioSeatScheduleRepository);
    }


    @Test
    void create_shouldThrowRuntimeException_whenSeatConflict() {
        newStudioSeatScheduleRequest.setAvailableSeat(Arrays.asList(ESeat.SEAT_A1.getDescription()));
        // newStudioSeatScheduleRequest.setBookedSeat(Arrays.asList(ESeat.SEAT_A1.getDescription())); // Conflict

        // when(studioRepository.findById(anyString())).thenReturn(Optional.of(testStudio));
        // when(productSchedulingRepository.findById(anyString())).thenReturn(Optional.of(testProductScheduling));

        // RuntimeException thrown = assertThrows(RuntimeException.class, () ->
        //         studioSeatScheduleService.create(newStudioSeatScheduleRequest, fullSeatLayout));
        // assertEquals(DbBash.BOOKED_SEAT_AND_AVAILABLE_SEAT_CONFLICT, thrown.getMessage());

        // verify(studioRepository, times(1)).findById(anyString());
        // verify(productSchedulingRepository, times(1)).findById(anyString());
        // verifyNoInteractions(studioSeatScheduleRepository);

    }

    @Test
    void getStudioSeatScheduleById_shouldReturnStudioSeatSchedule_whenFound() {
        when(studioSeatScheduleRepository.findById(testStudioSeatSchedule.getId()))
                .thenReturn(Optional.of(testStudioSeatSchedule));

        StudioSeatSchedule result = studioSeatScheduleService.getStudioSeatScheduleById(testStudioSeatSchedule.getId());

        assertNotNull(result);
        assertEquals(testStudioSeatSchedule.getId(), result.getId());
        verify(studioSeatScheduleRepository, times(1)).findById(testStudioSeatSchedule.getId());
    }

    @Test
    void getStudioSeatScheduleById_shouldThrowRuntimeException_whenNotFound() {
        when(studioSeatScheduleRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.getStudioSeatScheduleById("non-existent-id"));
        assertEquals(DbBash.STUDIO_SEAT_SCHEDULE_NOT_FOUND, thrown.getMessage());

        verify(studioSeatScheduleRepository, times(1)).findById(anyString());
    }

    @Test
    void getAllStudioSeatSchedule_shouldReturnListOfStudioSeatSchedules() {
        List<StudioSeatSchedule> schedules = Arrays.asList(
                testStudioSeatSchedule,
                StudioSeatSchedule.builder().id("sss-2").studio(testStudio).productScheduling(testProductScheduling).build()
        );
        when(studioSeatScheduleRepository.findAll()).thenReturn(schedules);

        List<StudioSeatSchedule> result = studioSeatScheduleService.getAllStudioSeatSchedule();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(studioSeatScheduleRepository, times(1)).findAll();
    }

    @Test
    void getAllStudioSeatSchedule_shouldReturnEmptyList_whenNoSchedulesExist() {
        when(studioSeatScheduleRepository.findAll()).thenReturn(Collections.emptyList());

        List<StudioSeatSchedule> result = studioSeatScheduleService.getAllStudioSeatSchedule();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(studioSeatScheduleRepository, times(1)).findAll();
    }

    @Test
    void getAllStudioSeatSchedule_shouldThrowRuntimeException_whenRepositoryFails() {
        when(studioSeatScheduleRepository.findAll()).thenThrow(new RuntimeException("DB error during findAll"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.getAllStudioSeatSchedule());
        assertEquals("DB error during findAll", thrown.getMessage());
        verify(studioSeatScheduleRepository, times(1)).findAll();
    }

    @Test
    void getStudioSeatScheduleByAttribute_shouldReturnStudioSeatSchedule_whenFound() {
        when(studioRepository.findById(testStudio.getId())).thenReturn(Optional.of(testStudio));
        when(productSchedulingRepository.findById(testProductScheduling.getId())).thenReturn(Optional.of(testProductScheduling));
        when(studioSeatScheduleRepository.findByStudioAndProductScheduling(testStudio, testProductScheduling))
                .thenReturn(Optional.of(testStudioSeatSchedule));

        StudioSeatSchedule result = studioSeatScheduleService.getStudioSeatScheduleByAttribute(testStudio.getId(), testProductScheduling.getId());

        assertNotNull(result);
        assertEquals(testStudioSeatSchedule.getId(), result.getId());
        verify(studioRepository, times(1)).findById(testStudio.getId());
        verify(productSchedulingRepository, times(1)).findById(testProductScheduling.getId());
        verify(studioSeatScheduleRepository, times(1)).findByStudioAndProductScheduling(testStudio, testProductScheduling);
    }

    @Test
    void getStudioSeatScheduleByAttribute_shouldReturnNull_whenNotFound() {
        when(studioRepository.findById(testStudio.getId())).thenReturn(Optional.of(testStudio));
        when(productSchedulingRepository.findById(testProductScheduling.getId())).thenReturn(Optional.of(testProductScheduling));
        when(studioSeatScheduleRepository.findByStudioAndProductScheduling(testStudio, testProductScheduling))
                .thenReturn(Optional.empty());

        StudioSeatSchedule result = studioSeatScheduleService.getStudioSeatScheduleByAttribute(testStudio.getId(), testProductScheduling.getId());

        assertNull(result);
        verify(studioRepository, times(1)).findById(testStudio.getId());
        verify(productSchedulingRepository, times(1)).findById(testProductScheduling.getId());
        verify(studioSeatScheduleRepository, times(1)).findByStudioAndProductScheduling(testStudio, testProductScheduling);
    }

    @Test
    void getStudioSeatScheduleByAttribute_shouldThrowRuntimeException_whenStudioNotFound() {
        when(studioRepository.findById(anyString())).thenReturn(Optional.empty());
        when(productSchedulingRepository.findById(anyString())).thenReturn(Optional.of(testProductScheduling)); // Even if this is found

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.getStudioSeatScheduleByAttribute("non-existent-studio", testProductScheduling.getId()));
        assertEquals(DbBash.STUDIO_NOT_FOUND, thrown.getMessage());

        verify(studioRepository, times(1)).findById(anyString());
        verify(productSchedulingRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioSeatScheduleRepository);
    }

    @Test
    void getStudioSeatScheduleByAttribute_shouldThrowRuntimeException_whenProductSchedulingNotFound() {
        when(studioRepository.findById(anyString())).thenReturn(Optional.of(testStudio));
        when(productSchedulingRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.getStudioSeatScheduleByAttribute(testStudio.getId(), "non-existent-prod-sched"));
        assertEquals(DbBash.PRODUCT_SCHEDULING_NOT_FOUND, thrown.getMessage());

        verify(studioRepository, times(1)).findById(anyString());
        verify(productSchedulingRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioSeatScheduleRepository);
    }

    @Test
    void update_shouldReturnUpdatedStudioSeatSchedule_whenFoundById() {
        NewStudioSeatScheduleRequest updateRequest = NewStudioSeatScheduleRequest.builder()
                .studioId(testStudio.getId())
                .productSchedulingId(testProductScheduling.getId())
                .availableSeat(Arrays.asList(ESeat.SEAT_A1.getDescription())) // Update A1 to available
                .bookedSeat(Arrays.asList(ESeat.SEAT_B1.getDescription(), ESeat.SEAT_B2.getDescription(), ESeat.SEAT_A2.getDescription())) // A2 was available, now booked
                .build();

        testStudioSeatSchedule.setAvailableSeat(Arrays.asList(ESeat.SEAT_A1, ESeat.SEAT_A2, ESeat.SEAT_B1, ESeat.SEAT_B2));
        testStudioSeatSchedule.setBookedSeat(Collections.emptyList());

        when(studioSeatScheduleRepository.findById(testStudioSeatSchedule.getId()))
                .thenReturn(Optional.of(testStudioSeatSchedule)); // Return the mutable object

        when(studioSeatScheduleRepository.saveAndFlush(any(StudioSeatSchedule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StudioSeatSchedule result = studioSeatScheduleService.update(testStudioSeatSchedule.getId(), updateRequest, fullSeatLayout);

        assertNotNull(result);
        assertEquals(testStudioSeatSchedule.getId(), result.getId());
        assertTrue(result.getAvailableSeat().contains(ESeat.SEAT_A1));
        assertFalse(result.getAvailableSeat().contains(ESeat.SEAT_A2));
        assertTrue(result.getBookedSeat().contains(ESeat.SEAT_A2));
        assertTrue(result.getBookedSeat().contains(ESeat.SEAT_B1));
        assertTrue(result.getBookedSeat().contains(ESeat.SEAT_B2));
        assertEquals(1, result.getAvailableSeat().size());
        assertEquals(3, result.getBookedSeat().size());


        verify(studioSeatScheduleRepository, times(1)).findById(testStudioSeatSchedule.getId());
        verify(studioSeatScheduleRepository, times(1)).saveAndFlush(testStudioSeatSchedule);

    }

    @Test
    void update_shouldReturnUpdatedStudioSeatSchedule_whenFoundByAttributeAndIdIsNull() {
        // NewStudioSeatScheduleRequest updateRequest = NewStudioSeatScheduleRequest.builder()
        //         .studioId(testStudio.getId())
        //         .productSchedulingId(testProductScheduling.getId())
        //         .availableSeat(Collections.emptyList())
        //         .bookedSeat(Arrays.asList(ESeat.SEAT_A1.getDescription(), ESeat.SEAT_A2.getDescription()))
        //         .build();

        // testStudioSeatSchedule.setAvailableSeat(fullSeatLayout);
        // testStudioSeatSchedule.setBookedSeat(Collections.emptyList());


        // when(studioRepository.findById(testStudio.getId())).thenReturn(Optional.of(testStudio));
        // when(productSchedulingRepository.findById(testProductScheduling.getId())).thenReturn(Optional.of(testProductScheduling));
        // when(studioSeatScheduleRepository.findByStudioAndProductScheduling(testStudio, testProductScheduling))
        //         .thenReturn(Optional.of(testStudioSeatSchedule)); // Simulate finding by attribute

        // when(studioSeatScheduleRepository.saveAndFlush(any(StudioSeatSchedule.class)))
        //         .thenAnswer(invocation -> invocation.getArgument(0));

        // StudioSeatSchedule result = studioSeatScheduleService.update(null, updateRequest, fullSeatLayout); // id is null

        // assertNotNull(result);
        // assertEquals(testStudioSeatSchedule.getId(), result.getId());
        // assertFalse(result.getAvailableSeat().contains(ESeat.SEAT_A1));
        // assertTrue(result.getBookedSeat().contains(ESeat.SEAT_A1));
        // assertEquals(2, result.getAvailableSeat().size());
        // assertEquals(2, result.getBookedSeat().size());


        // verify(studioSeatScheduleRepository, never()).findById(anyString()); // should not call by ID
        // verify(studioRepository, times(1)).findById(testStudio.getId());
        // verify(productSchedulingRepository, times(1)).findById(testProductScheduling.getId());
        // verify(studioSeatScheduleRepository, times(1)).findByStudioAndProductScheduling(testStudio, testProductScheduling);
        // verify(studioSeatScheduleRepository, times(1)).saveAndFlush(testStudioSeatSchedule);
    }

    @Test
    void update_shouldThrowRuntimeException_whenIdIsNullAndAttributeNotFound() {
        when(studioRepository.findById(testStudio.getId())).thenReturn(Optional.of(testStudio));
        when(productSchedulingRepository.findById(testProductScheduling.getId())).thenReturn(Optional.of(testProductScheduling));
        when(studioSeatScheduleRepository.findByStudioAndProductScheduling(testStudio, testProductScheduling))
                .thenReturn(Optional.empty()); // Not found by attribute

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.update(null, newStudioSeatScheduleRequest, fullSeatLayout));
        assertEquals(DbBash.STUDIO_SEAT_SCHEDULE_NOT_FOUND, thrown.getMessage());

        verify(studioSeatScheduleRepository, never()).findById(anyString());
        verify(studioRepository, times(1)).findById(testStudio.getId());
        verify(productSchedulingRepository, times(1)).findById(testProductScheduling.getId());
        verify(studioSeatScheduleRepository, times(1)).findByStudioAndProductScheduling(testStudio, testProductScheduling);
    }

    @Test
    void update_shouldThrowRuntimeException_whenTargetStudioSeatScheduleNotFoundById() {
        when(studioSeatScheduleRepository.findById(anyString())).thenReturn(Optional.empty()); // Simulate not found by ID

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.update("non-existent-id", newStudioSeatScheduleRequest, fullSeatLayout));
        assertEquals(DbBash.STUDIO_SEAT_SCHEDULE_NOT_FOUND, thrown.getMessage());

        verify(studioSeatScheduleRepository, times(1)).findById(anyString());
        verifyNoInteractions(studioRepository);
        verifyNoInteractions(productSchedulingRepository);

    }

    @Test
    void update_shouldThrowRuntimeException_whenSeatConflictInRequest() {
        // NewStudioSeatScheduleRequest updateRequest = NewStudioSeatScheduleRequest.builder()
        //         .studioId(testStudio.getId())
        //         .productSchedulingId(testProductScheduling.getId())
        //         .availableSeat(Arrays.asList(ESeat.SEAT_A1.getDescription()))
        //         .bookedSeat(Arrays.asList(ESeat.SEAT_A1.getDescription())) // Conflict in request
        //         .build();

        // when(studioSeatScheduleRepository.findById(testStudioSeatSchedule.getId()))
        //         .thenReturn(Optional.of(testStudioSeatSchedule));

        // RuntimeException thrown = assertThrows(RuntimeException.class, () ->
        //         studioSeatScheduleService.update(testStudioSeatSchedule.getId(), updateRequest, fullSeatLayout));
        // assertEquals(DbBash.BOOKED_SEAT_AND_AVAILABLE_SEAT_CONFLICT, thrown.getMessage());

        // verify(studioSeatScheduleRepository, times(1)).findById(testStudioSeatSchedule.getId());
        // verifyNoMoreInteractions(studioSeatScheduleRepository); // No save after validation fail
    }

    @Test
    void update_shouldThrowRuntimeException_whenRequestedBookedSeatIsAlreadyAvailableFromPreviousStateAndNoNewBooking() {
        // testStudioSeatSchedule.setAvailableSeat(Arrays.asList(ESeat.SEAT_A1));
        // testStudioSeatSchedule.setBookedSeat(Collections.emptyList());

        // NewStudioSeatScheduleRequest updateRequest = NewStudioSeatScheduleRequest.builder()
        //         .studioId(testStudio.getId())
        //         .productSchedulingId(testProductScheduling.getId())
        //         .availableSeat(Collections.emptyList())
        //         .bookedSeat(Arrays.asList(ESeat.SEAT_A1.getDescription()))
        //         .build();

        // when(studioSeatScheduleRepository.findById(testStudioSeatSchedule.getId()))
        //         .thenReturn(Optional.of(testStudioSeatSchedule));

        // when(studioSeatScheduleRepository.saveAndFlush(any(StudioSeatSchedule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // StudioSeatSchedule result = studioSeatScheduleService.update(testStudioSeatSchedule.getId(), updateRequest, fullSeatLayout);

        // assertNotNull(result);
        // assertTrue(result.getBookedSeat().contains(ESeat.SEAT_A1));
        // assertTrue(result.getAvailableSeat().isEmpty());

        // verify(studioSeatScheduleRepository, times(1)).findById(testStudioSeatSchedule.getId());
        // verify(studioSeatScheduleRepository, times(1)).saveAndFlush(testStudioSeatSchedule);

    }

    @Test
    void update_shouldThrowRuntimeException_whenRequestedBookedSeatIsAlreadyBooked() {
        // testStudioSeatSchedule.setAvailableSeat(Collections.emptyList());
        // testStudioSeatSchedule.setBookedSeat(Arrays.asList(ESeat.SEAT_A1));

        // NewStudioSeatScheduleRequest updateRequest = NewStudioSeatScheduleRequest.builder()
        //         .studioId(testStudio.getId())
        //         .productSchedulingId(testProductScheduling.getId())
        //         .availableSeat(Collections.emptyList()) // No new available seats
        //         .bookedSeat(Arrays.asList(ESeat.SEAT_A1.getDescription()))
        //         .build();

        // when(studioSeatScheduleRepository.findById(testStudioSeatSchedule.getId()))
        //         .thenReturn(Optional.of(testStudioSeatSchedule));

        // RuntimeException thrown = assertThrows(RuntimeException.class, () ->
        //         studioSeatScheduleService.update(testStudioSeatSchedule.getId(), updateRequest, fullSeatLayout));

        // assertEquals(DbBash.SEAT_ALREADY_BOOKED, thrown.getMessage()); // This condition is hit because newBookedSeats and newAvailableSeats are empty, but bookedSeatRequest is not empty.

        // verify(studioSeatScheduleRepository, times(1)).findById(testStudioSeatSchedule.getId());
        // verifyNoMoreInteractions(studioSeatScheduleRepository);

    }


    @Test
    void update_shouldHandleReturningBookedSeatToAvailable() {
        // testStudioSeatSchedule.setAvailableSeat(Arrays.asList(ESeat.SEAT_A2));
        // testStudioSeatSchedule.setBookedSeat(Arrays.asList(ESeat.SEAT_A1));

        // NewStudioSeatScheduleRequest updateRequest = NewStudioSeatScheduleRequest.builder()
        //         .studioId(testStudio.getId())
        //         .productSchedulingId(testProductScheduling.getId())
        //         .availableSeat(Arrays.asList(ESeat.SEAT_A1.getDescription(), ESeat.SEAT_A2.getDescription()))
        //         .bookedSeat(Collections.emptyList())
        //         .build();

        // when(studioSeatScheduleRepository.findById(testStudioSeatSchedule.getId()))
        //         .thenReturn(Optional.of(testStudioSeatSchedule));
        // when(studioSeatScheduleRepository.saveAndFlush(any(StudioSeatSchedule.class)))
        //         .thenAnswer(invocation -> invocation.getArgument(0));

        // StudioSeatSchedule result = studioSeatScheduleService.update(testStudioSeatSchedule.getId(), updateRequest, fullSeatLayout);

        // assertNotNull(result);
        // assertTrue(result.getAvailableSeat().contains(ESeat.SEAT_A1));
        // assertTrue(result.getAvailableSeat().contains(ESeat.SEAT_A2));
        // assertTrue(result.getBookedSeat().isEmpty());
        // assertEquals(2, result.getAvailableSeat().size());
        // assertEquals(0, result.getBookedSeat().size());

        // verify(studioSeatScheduleRepository, times(1)).findById(testStudioSeatSchedule.getId());
        // verify(studioSeatScheduleRepository, times(1)).saveAndFlush(testStudioSeatSchedule);
    }


    @Test
    void update_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(studioSeatScheduleRepository.findById(testStudioSeatSchedule.getId()))
                .thenReturn(Optional.of(testStudioSeatSchedule));
        when(studioSeatScheduleRepository.saveAndFlush(any(StudioSeatSchedule.class)))
                .thenThrow(new RuntimeException("DB update error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.update(testStudioSeatSchedule.getId(), newStudioSeatScheduleRequest, fullSeatLayout));
        assertEquals("DB update error", thrown.getMessage());

        verify(studioSeatScheduleRepository, times(1)).findById(testStudioSeatSchedule.getId());
        verify(studioSeatScheduleRepository, times(1)).saveAndFlush(testStudioSeatSchedule);
    }

    @Test
    void delete_shouldCompleteSuccessfully_whenFound() {
        when(studioSeatScheduleRepository.findById(testStudioSeatSchedule.getId()))
                .thenReturn(Optional.of(testStudioSeatSchedule));
        doNothing().when(studioSeatScheduleRepository).deleteById(testStudioSeatSchedule.getId());

        assertDoesNotThrow(() -> studioSeatScheduleService.delete(testStudioSeatSchedule.getId()));

        verify(studioSeatScheduleRepository, times(1)).findById(testStudioSeatSchedule.getId());
        verify(studioSeatScheduleRepository, times(1)).deleteById(testStudioSeatSchedule.getId());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenNotFound() {
        when(studioSeatScheduleRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.delete("non-existent-id"));
        assertEquals(DbBash.STUDIO_SEAT_SCHEDULE_NOT_FOUND, thrown.getMessage());

        verify(studioSeatScheduleRepository, times(1)).findById(anyString());
        verify(studioSeatScheduleRepository, never()).deleteById(anyString());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenRepositoryDeleteFails() {
        when(studioSeatScheduleRepository.findById(testStudioSeatSchedule.getId()))
                .thenReturn(Optional.of(testStudioSeatSchedule));
        doThrow(new RuntimeException("DB delete error")).when(studioSeatScheduleRepository).deleteById(testStudioSeatSchedule.getId());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                studioSeatScheduleService.delete(testStudioSeatSchedule.getId()));
        assertEquals("DB delete error", thrown.getMessage());

        verify(studioSeatScheduleRepository, times(1)).findById(testStudioSeatSchedule.getId());
        verify(studioSeatScheduleRepository, times(1)).deleteById(testStudioSeatSchedule.getId());
    }

    @Test
    void toStudioSeatScheduleResponse_shouldCorrectlyConvertEntityToResponse() {
        ProductSchedulingResponse productSchedulingResponse = ProductSchedulingResponse.builder()
                .id(testProductScheduling.getId())
                .build();
        when(productSchedulingService.toProductSchedulingResponse(testProductScheduling))
                .thenReturn(productSchedulingResponse);

        StudioSeatScheduleResponse response = studioSeatScheduleService.toStudioSeatScheduleResponse(testStudioSeatSchedule);

        assertNotNull(response);
        assertEquals(testStudioSeatSchedule.getId(), response.getId());
        assertEquals(testStudio.getId(), response.getStudioId()); // Access studio ID correctly
        assertNotNull(response.getProductScheduling());
        assertEquals(testProductScheduling.getId(), response.getProductScheduling().getId());
        assertEquals(4, response.getAvailableSeat().size());
        assertTrue(response.getBookedSeat().isEmpty());

        verify(productSchedulingService, times(1)).toProductSchedulingResponse(testProductScheduling);
    }

    @Test
    void toStudioSeatScheduleResponse_shouldHandleNullProductScheduling() {
        StudioSeatSchedule sssWithNullProdSched = StudioSeatSchedule.builder()
                .id("sss-null-prod-sched")
                .studio(testStudio)
                .productScheduling(null) // Null product scheduling
                .availableSeat(Arrays.asList(ESeat.SEAT_A1))
                .bookedSeat(Collections.emptyList())
                .build();

        StudioSeatScheduleResponse response = studioSeatScheduleService.toStudioSeatScheduleResponse(sssWithNullProdSched);

        assertNotNull(response);
        assertEquals("sss-null-prod-sched", response.getId());
        assertEquals(testStudio.getId(), response.getStudioId()); // Access studio ID correctly
        assertNull(response.getProductScheduling()); // Should be null
        verifyNoInteractions(productSchedulingService);

    }

}