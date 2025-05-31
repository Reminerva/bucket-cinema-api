package com.flix.flix.service.impl;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ESchedule;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.model.request.NewProductSchedulingRequest;
import com.flix.flix.model.response.ProductSchedulingResponse;
import com.flix.flix.repository.ProductSchedulingRepository;
import com.flix.flix.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
public class ProductSchedulingServiceImplTest {

    @Mock
    private ProductSchedulingRepository productSchedulingRepository;

    @Mock
    private ProductService productService; // Mock the ProductService dependency

    @InjectMocks
    private ProductSchedulingServiceImpl productSchedulingService;

    private Product testProduct;
    private ProductScheduling testProductScheduling;
    private NewProductSchedulingRequest newProductSchedulingRequest;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("prod-id-123")
                .title("The Amazing Film")
                .build();

        testProductScheduling = ProductScheduling.builder()
                .id("sched-id-456")
                .schedule(ESchedule.SCHEDULE_9_00)
                .productIdScheduling(testProduct)
                .build();

        newProductSchedulingRequest = NewProductSchedulingRequest.builder()
                .schedule(ESchedule.SCHEDULE_11_20.getDescription()) // Use description for request
                .productId(testProduct.getId())
                .build();
    }

    @Test
    void create_shouldReturnProductScheduling_whenSuccessful() {
        when(productService.getProductById(testProduct.getId())).thenReturn(testProduct);

        when(productSchedulingRepository.saveAndFlush(any(ProductScheduling.class))).thenAnswer(invocation -> {
            ProductScheduling savedScheduling = invocation.getArgument(0);
            savedScheduling.setId("new-sched-id"); // Simulate ID generation by the repository
            return savedScheduling;
        });

        try (MockedStatic<ESchedule> mockedESchedule = mockStatic(ESchedule.class)) {
            mockedESchedule.when(() -> ESchedule.findByDescription(newProductSchedulingRequest.getSchedule()))
                    .thenReturn(ESchedule.SCHEDULE_11_20);

            ProductScheduling createdScheduling = productSchedulingService.create(newProductSchedulingRequest);

            assertNotNull(createdScheduling);
            assertEquals("new-sched-id", createdScheduling.getId());
            assertEquals(ESchedule.SCHEDULE_11_20, createdScheduling.getSchedule());
            assertEquals(testProduct.getId(), createdScheduling.getProductIdScheduling().getId());

            verify(productService, times(1)).getProductById(testProduct.getId());
            verify(productSchedulingRepository, times(1)).saveAndFlush(any(ProductScheduling.class));
            mockedESchedule.verify(() -> ESchedule.findByDescription(newProductSchedulingRequest.getSchedule()), times(1));
        }
    }

    @Test
    void create_shouldThrowRuntimeException_whenProductNotFound() {
        when(productService.getProductById(anyString())).thenThrow(new RuntimeException(DbBash.PRODUCT_NOT_FOUND));

        try (MockedStatic<ESchedule> mockedESchedule = mockStatic(ESchedule.class)) {
            mockedESchedule.when(() -> ESchedule.findByDescription(anyString()))
                     .thenReturn(ESchedule.SCHEDULE_11_20); // Simulate successful enum parsing

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productSchedulingService.create(newProductSchedulingRequest));

            assertEquals(DbBash.PRODUCT_NOT_FOUND, thrown.getMessage());

            verify(productService, times(1)).getProductById(anyString());
            verifyNoInteractions(productSchedulingRepository);
        }
    }

    @Test
    void create_shouldThrowRuntimeException_whenScheduleDescriptionInvalid() {

        try (MockedStatic<ESchedule> mockedESchedule = mockStatic(ESchedule.class)) {
            mockedESchedule.when(() -> ESchedule.findByDescription(anyString()))
                    .thenThrow(new IllegalArgumentException("Invalid schedule description"));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productSchedulingService.create(newProductSchedulingRequest));

            assertEquals("Invalid schedule description", thrown.getMessage());

            verify(productService, times(0)).getProductById(testProduct.getId());
            mockedESchedule.verify(() -> ESchedule.findByDescription(anyString()), times(1));
            verifyNoInteractions(productSchedulingRepository);
        }
    }

    @Test
    void create_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(productService.getProductById(testProduct.getId())).thenReturn(testProduct);

        when(productSchedulingRepository.saveAndFlush(any(ProductScheduling.class)))
                .thenThrow(new RuntimeException("DB save error"));

        try (MockedStatic<ESchedule> mockedESchedule = mockStatic(ESchedule.class)) {
            mockedESchedule.when(() -> ESchedule.findByDescription(anyString()))
                    .thenReturn(ESchedule.SCHEDULE_11_20);

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productSchedulingService.create(newProductSchedulingRequest));

            assertEquals("DB save error", thrown.getMessage());

            verify(productService, times(1)).getProductById(testProduct.getId());
            verify(productSchedulingRepository, times(1)).saveAndFlush(any(ProductScheduling.class));
            mockedESchedule.verify(() -> ESchedule.findByDescription(anyString()), times(1));
        }
    }

    @Test
    void getAll_shouldReturnListOfProductSchedulings() {
        ProductScheduling anotherScheduling = ProductScheduling.builder()
                .id("sched-id-789")
                .schedule(ESchedule.SCHEDULE_19_40)
                .productIdScheduling(testProduct)
                .build();
        List<ProductScheduling> schedulings = Arrays.asList(testProductScheduling, anotherScheduling);

        when(productSchedulingRepository.findAll()).thenReturn(schedulings);

        List<ProductScheduling> result = productSchedulingService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testProductScheduling.getId(), result.get(0).getId());
        assertEquals(anotherScheduling.getId(), result.get(1).getId());

        verify(productSchedulingRepository, times(1)).findAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoSchedulingsExist() {
        when(productSchedulingRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductScheduling> result = productSchedulingService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productSchedulingRepository, times(1)).findAll();
    }

    @Test
    void getProductSchedulingById_shouldReturnProductScheduling_whenFound() {
        when(productSchedulingRepository.findById(testProductScheduling.getId()))
                .thenReturn(Optional.of(testProductScheduling));

        ProductScheduling foundScheduling = productSchedulingService.getProductSchedulingById(testProductScheduling.getId());

        assertNotNull(foundScheduling);
        assertEquals(testProductScheduling.getId(), foundScheduling.getId());

        verify(productSchedulingRepository, times(1)).findById(testProductScheduling.getId());
    }

    @Test
    void getProductSchedulingById_shouldThrowRuntimeException_whenNotFound() {
        when(productSchedulingRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productSchedulingService.getProductSchedulingById("non-existent-id"));

        assertEquals(DbBash.PRODUCT_SCHEDULING_NOT_FOUND, thrown.getMessage());

        verify(productSchedulingRepository, times(1)).findById(anyString());
    }

    @Test
    void update_shouldReturnUpdatedProductScheduling_whenSuccessful() {
        NewProductSchedulingRequest updateRequest = NewProductSchedulingRequest.builder()
                .schedule(ESchedule.SCHEDULE_19_40.getDescription())
                .productId(testProduct.getId()) // Product ID generally not updated here, but included for completeness
                .build();

        when(productSchedulingRepository.findById(testProductScheduling.getId()))
                .thenReturn(Optional.of(testProductScheduling));

        when(productSchedulingRepository.saveAndFlush(any(ProductScheduling.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<ESchedule> mockedESchedule = mockStatic(ESchedule.class)) {
            mockedESchedule.when(() -> ESchedule.findByDescription(updateRequest.getSchedule()))
                    .thenReturn(ESchedule.SCHEDULE_19_40);

            ProductScheduling updatedScheduling = productSchedulingService.update(testProductScheduling.getId(), updateRequest);

            assertNotNull(updatedScheduling);
            assertEquals(testProductScheduling.getId(), updatedScheduling.getId()); // ID remains the same
            assertEquals(ESchedule.SCHEDULE_19_40, updatedScheduling.getSchedule());

            assertEquals(ESchedule.SCHEDULE_19_40, testProductScheduling.getSchedule());

            verify(productSchedulingRepository, times(1)).findById(testProductScheduling.getId());
            verify(productSchedulingRepository, times(1)).saveAndFlush(testProductScheduling); // Verify save was called with the modified object
            mockedESchedule.verify(() -> ESchedule.findByDescription(updateRequest.getSchedule()), times(1));
        }
    }

    @Test
    void update_shouldThrowRuntimeException_whenProductSchedulingNotFound() {
        when(productSchedulingRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productSchedulingService.update("non-existent-id", newProductSchedulingRequest));

        assertEquals(DbBash.PRODUCT_SCHEDULING_NOT_FOUND, thrown.getMessage());

        verify(productSchedulingRepository, times(1)).findById(anyString());
        verify(productSchedulingRepository, never()).saveAndFlush(any(ProductScheduling.class));
    }

    @Test
    void update_shouldThrowRuntimeException_whenScheduleDescriptionInvalid() {
        when(productSchedulingRepository.findById(testProductScheduling.getId()))
                .thenReturn(Optional.of(testProductScheduling));

        try (MockedStatic<ESchedule> mockedESchedule = mockStatic(ESchedule.class)) {
            mockedESchedule.when(() -> ESchedule.findByDescription(anyString()))
                    .thenThrow(new IllegalArgumentException("Invalid schedule description for update"));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productSchedulingService.update(testProductScheduling.getId(), newProductSchedulingRequest));

            assertEquals("Invalid schedule description for update", thrown.getMessage());

            verify(productSchedulingRepository, times(1)).findById(testProductScheduling.getId());
            mockedESchedule.verify(() -> ESchedule.findByDescription(anyString()), times(1));
            verify(productSchedulingRepository, never()).saveAndFlush(any(ProductScheduling.class));
        }
    }

    @Test
    void update_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(productSchedulingRepository.findById(testProductScheduling.getId()))
                .thenReturn(Optional.of(testProductScheduling));

        when(productSchedulingRepository.saveAndFlush(any(ProductScheduling.class)))
                .thenThrow(new RuntimeException("DB update error"));

        try (MockedStatic<ESchedule> mockedESchedule = mockStatic(ESchedule.class)) {
            mockedESchedule.when(() -> ESchedule.findByDescription(anyString()))
                    .thenReturn(ESchedule.SCHEDULE_9_00);

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productSchedulingService.update(testProductScheduling.getId(), newProductSchedulingRequest));

            assertEquals("DB update error", thrown.getMessage());

            verify(productSchedulingRepository, times(1)).findById(testProductScheduling.getId());
            verify(productSchedulingRepository, times(1)).saveAndFlush(testProductScheduling);
            mockedESchedule.verify(() -> ESchedule.findByDescription(anyString()), times(1));
        }
    }

    @Test
    void delete_shouldCompleteSuccessfully_whenFound() {
        when(productSchedulingRepository.findById(testProductScheduling.getId()))
                .thenReturn(Optional.of(testProductScheduling));

        doNothing().when(productSchedulingRepository).deleteById(testProductScheduling.getId());

        assertDoesNotThrow(() -> productSchedulingService.delete(testProductScheduling.getId()));

        verify(productSchedulingRepository, times(1)).findById(testProductScheduling.getId());
        verify(productSchedulingRepository, times(1)).deleteById(testProductScheduling.getId());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenProductSchedulingNotFound() {
        when(productSchedulingRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productSchedulingService.delete("non-existent-id"));

        assertEquals(DbBash.PRODUCT_SCHEDULING_NOT_FOUND, thrown.getMessage());

        verify(productSchedulingRepository, times(1)).findById(anyString());
        verify(productSchedulingRepository, never()).deleteById(anyString()); // Delete should not be called
    }

    @Test
    void delete_shouldThrowRuntimeException_whenRepositoryDeleteFails() {
        when(productSchedulingRepository.findById(testProductScheduling.getId()))
                .thenReturn(Optional.of(testProductScheduling));

        doThrow(new RuntimeException("DB delete error")).when(productSchedulingRepository).deleteById(testProductScheduling.getId());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productSchedulingService.delete(testProductScheduling.getId()));

        assertEquals("DB delete error", thrown.getMessage());

        verify(productSchedulingRepository, times(1)).findById(testProductScheduling.getId());
        verify(productSchedulingRepository, times(1)).deleteById(testProductScheduling.getId());
    }

    @Test
    void toProductSchedulingResponse_shouldCorrectlyConvertEntityToResponse() {
        ProductSchedulingResponse response = productSchedulingService.toProductSchedulingResponse(testProductScheduling);

        assertNotNull(response);
        assertEquals(testProductScheduling.getId(), response.getId());
        assertEquals(testProductScheduling.getSchedule().getDescription(), response.getSchedule());
        assertEquals(testProduct.getId(), response.getProductId());
    }

    @Test
    void toProductSchedulingResponse_shouldHandleNullProductIdScheduling() {
        ProductScheduling schedulingWithNullProduct = ProductScheduling.builder()
                .id("sched-null-prod")
                .schedule(ESchedule.SCHEDULE_9_00)
                .productIdScheduling(null) // Set product to null
                .build();

        ProductSchedulingResponse response = productSchedulingService.toProductSchedulingResponse(schedulingWithNullProduct);

        assertNotNull(response);
        assertEquals("sched-null-prod", response.getId());
        assertEquals(ESchedule.SCHEDULE_9_00.getDescription(), response.getSchedule());
        assertNull(response.getProductId()); // Should be null
    }

    @Test
    void getProductSchedulingByAttribute_shouldReturnProductScheduling_whenFound() {
        when(productService.getProductById(newProductSchedulingRequest.getProductId())).thenReturn(testProduct);

        try (MockedStatic<ESchedule> mockedESchedule = mockStatic(ESchedule.class)) {
            mockedESchedule.when(() -> ESchedule.findByDescription(newProductSchedulingRequest.getSchedule()))
                    .thenReturn(ESchedule.SCHEDULE_11_20);

            when(productSchedulingRepository.findProductSchedulingByScheduleAndProductIdScheduling(
                    ESchedule.SCHEDULE_11_20, testProduct))
                    .thenReturn(Optional.of(testProductScheduling));

            ProductScheduling foundScheduling = productSchedulingService.getProductSchedulingByAttribute(newProductSchedulingRequest);

            assertNotNull(foundScheduling);
            assertEquals(testProductScheduling.getId(), foundScheduling.getId());

            verify(productService, times(1)).getProductById(newProductSchedulingRequest.getProductId());
            mockedESchedule.verify(() -> ESchedule.findByDescription(newProductSchedulingRequest.getSchedule()), times(1));
            verify(productSchedulingRepository, times(1)).findProductSchedulingByScheduleAndProductIdScheduling(
                    any(ESchedule.class), any(Product.class));
        }
    }

    @Test
    void getProductSchedulingByAttribute_shouldReturnNull_whenNotFound() {
        when(productService.getProductById(newProductSchedulingRequest.getProductId())).thenReturn(testProduct);

        try (MockedStatic<ESchedule> mockedESchedule = mockStatic(ESchedule.class)) {
            mockedESchedule.when(() -> ESchedule.findByDescription(newProductSchedulingRequest.getSchedule()))
                    .thenReturn(ESchedule.SCHEDULE_11_20);

            when(productSchedulingRepository.findProductSchedulingByScheduleAndProductIdScheduling(
                    any(ESchedule.class), any(Product.class)))
                    .thenReturn(Optional.empty());

            ProductScheduling foundScheduling = productSchedulingService.getProductSchedulingByAttribute(newProductSchedulingRequest);

            assertNull(foundScheduling);

            verify(productService, times(1)).getProductById(newProductSchedulingRequest.getProductId());
            mockedESchedule.verify(() -> ESchedule.findByDescription(newProductSchedulingRequest.getSchedule()), times(1));
            verify(productSchedulingRepository, times(1)).findProductSchedulingByScheduleAndProductIdScheduling(
                    any(ESchedule.class), any(Product.class));
        }
    }

    @Test
    void getProductSchedulingByAttribute_shouldThrowRuntimeException_whenProductServiceFails() {
        when(productService.getProductById(anyString())).thenThrow(new RuntimeException(DbBash.PRODUCT_NOT_FOUND));

        try (MockedStatic<ESchedule> mockedESchedule = mockStatic(ESchedule.class)) {
            mockedESchedule.when(() -> ESchedule.findByDescription(anyString()))
                    .thenReturn(ESchedule.SCHEDULE_11_20);

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productSchedulingService.getProductSchedulingByAttribute(newProductSchedulingRequest));

            assertEquals(DbBash.PRODUCT_NOT_FOUND, thrown.getMessage());

            verify(productService, times(1)).getProductById(anyString());
            mockedESchedule.verify(() -> ESchedule.findByDescription(anyString()), times(1));
            verifyNoInteractions(productSchedulingRepository); // No repository call if product service failed
        }
    }

    @Test
    void getProductSchedulingByAttribute_shouldThrowRuntimeException_whenScheduleDescriptionInvalid() {

        try (MockedStatic<ESchedule> mockedESchedule = mockStatic(ESchedule.class)) {
            mockedESchedule.when(() -> ESchedule.findByDescription(anyString()))
                    .thenThrow(new IllegalArgumentException("Invalid schedule description for attribute search"));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    productSchedulingService.getProductSchedulingByAttribute(newProductSchedulingRequest));

            assertEquals("Invalid schedule description for attribute search", thrown.getMessage());

            verify(productService, never()).getProductById(anyString()); // Product service might not be called first depending on order
            mockedESchedule.verify(() -> ESchedule.findByDescription(anyString()), times(1));
            verifyNoInteractions(productSchedulingRepository);
        }

    }
}