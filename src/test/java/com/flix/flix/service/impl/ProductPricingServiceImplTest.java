package com.flix.flix.service.impl;

import com.flix.flix.constant.DbBash;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductPricing;
import com.flix.flix.model.request.NewProductPricingRequest;
import com.flix.flix.model.response.ProductPricingResponse;
import com.flix.flix.repository.ProductPricingRepository;
import com.flix.flix.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductPricingServiceImplTest {

    @Mock
    private ProductPricingRepository productPricingRepository;

    @Mock
    private ProductService productService; // Mock the ProductService dependency

    @InjectMocks
    private ProductPricingServiceImpl productPricingService;

    private Product testProduct;
    private ProductPricing testProductPricing;
    private NewProductPricingRequest newProductPricingRequest;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("prod-id-123")
                .title("Test Movie")
                .build();

        testProductPricing = ProductPricing.builder()
                .id("price-id-456")
                .weekdayPrice(10.0)
                .weekendPrice(12.0)
                .isPriceActive(true)
                .priceDate(LocalDate.now())
                .productIdPricing(testProduct)
                .build();

        newProductPricingRequest = NewProductPricingRequest.builder()
                .weekdayPrice(15.0)
                .weekendPrice(18.0)
                .productId(testProduct.getId())
                .isPriceActive(true) // For update/getByAttribute tests
                .build();
    }

    @Test
    void create_shouldReturnProductPricing_whenSuccessful() {
        when(productService.getProductById(testProduct.getId())).thenReturn(testProduct);

        when(productPricingRepository.saveAndFlush(any(ProductPricing.class))).thenAnswer(invocation -> {
            ProductPricing savedPricing = invocation.getArgument(0);
            savedPricing.setId("new-price-id"); // Simulate ID generation
            return savedPricing;
        });

        ProductPricing createdPricing = productPricingService.create(newProductPricingRequest);

        assertNotNull(createdPricing);
        assertEquals("new-price-id", createdPricing.getId());
        assertEquals(newProductPricingRequest.getWeekdayPrice(), createdPricing.getWeekdayPrice());
        assertEquals(newProductPricingRequest.getWeekendPrice(), createdPricing.getWeekendPrice());
        assertTrue(createdPricing.getIsPriceActive()); // Should be true by default in create
        assertEquals(LocalDate.now(), createdPricing.getPriceDate());
        assertEquals(testProduct.getId(), createdPricing.getProductIdPricing().getId());

        verify(productService, times(1)).getProductById(testProduct.getId());
        verify(productPricingRepository, times(1)).saveAndFlush(any(ProductPricing.class));
    }

    @Test
    void create_shouldThrowRuntimeException_whenProductNotFound() {
        when(productService.getProductById(anyString())).thenThrow(new RuntimeException(DbBash.PRODUCT_NOT_FOUND));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productPricingService.create(newProductPricingRequest));

        assertEquals(DbBash.PRODUCT_NOT_FOUND, thrown.getMessage()); // The service re-throws the original exception

        verify(productService, times(1)).getProductById(anyString());
        verifyNoInteractions(productPricingRepository); // No save should happen
    }

    @Test
    void create_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(productService.getProductById(testProduct.getId())).thenReturn(testProduct);

        when(productPricingRepository.saveAndFlush(any(ProductPricing.class)))
                .thenThrow(new RuntimeException("DB save error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productPricingService.create(newProductPricingRequest));

        assertEquals("DB save error", thrown.getMessage()); // The service re-throws the original exception

        verify(productService, times(1)).getProductById(testProduct.getId());
        verify(productPricingRepository, times(1)).saveAndFlush(any(ProductPricing.class));
    }

    @Test
    void getAll_shouldReturnListOfProductPricings() {
        ProductPricing anotherPricing = ProductPricing.builder()
                .id("price-id-789")
                .weekdayPrice(20.0)
                .weekendPrice(25.0)
                .isPriceActive(false)
                .priceDate(LocalDate.of(2024, 1, 1))
                .productIdPricing(testProduct)
                .build();
        List<ProductPricing> pricings = Arrays.asList(testProductPricing, anotherPricing);

        when(productPricingRepository.findAll()).thenReturn(pricings);

        List<ProductPricing> result = productPricingService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testProductPricing.getId(), result.get(0).getId());
        assertEquals(anotherPricing.getId(), result.get(1).getId());

        verify(productPricingRepository, times(1)).findAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoPricingsExist() {
        when(productPricingRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductPricing> result = productPricingService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productPricingRepository, times(1)).findAll();
    }

    @Test
    void getProductPricingById_shouldReturnProductPricing_whenFound() {
        when(productPricingRepository.findById(testProductPricing.getId()))
                .thenReturn(Optional.of(testProductPricing));

        ProductPricing foundPricing = productPricingService.getProductPricingById(testProductPricing.getId());

        assertNotNull(foundPricing);
        assertEquals(testProductPricing.getId(), foundPricing.getId());

        verify(productPricingRepository, times(1)).findById(testProductPricing.getId());
    }

    @Test
    void getProductPricingById_shouldThrowRuntimeException_whenNotFound() {
        when(productPricingRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productPricingService.getProductPricingById("non-existent-id"));

        assertEquals(DbBash.PRODUCT_PRICING_NOT_FOUND, thrown.getMessage());

        verify(productPricingRepository, times(1)).findById(anyString());
    }

    @Test
    void update_shouldReturnUpdatedProductPricing_whenSuccessful() {
        NewProductPricingRequest updateRequest = NewProductPricingRequest.builder()
                .weekdayPrice(20.0)
                .weekendPrice(22.0)
                .isPriceActive(false)
                .productId(testProduct.getId()) // Product ID usually not updated here, but included for completeness
                .build();

        when(productPricingRepository.findById(testProductPricing.getId()))
                .thenReturn(Optional.of(testProductPricing)); // Return the original object to be modified

        when(productPricingRepository.saveAndFlush(any(ProductPricing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // Return the modified object

        ProductPricing updatedPricing = productPricingService.update(testProductPricing.getId(), updateRequest);

        assertNotNull(updatedPricing);
        assertEquals(testProductPricing.getId(), updatedPricing.getId()); // ID remains the same
        assertEquals(updateRequest.getWeekdayPrice(), updatedPricing.getWeekdayPrice());
        assertEquals(updateRequest.getWeekendPrice(), updatedPricing.getWeekendPrice());
        assertEquals(updateRequest.getIsPriceActive(), updatedPricing.getIsPriceActive());

        assertEquals(updateRequest.getWeekdayPrice(), testProductPricing.getWeekdayPrice());
        assertEquals(updateRequest.getWeekendPrice(), testProductPricing.getWeekendPrice());
        assertEquals(updateRequest.getIsPriceActive(), testProductPricing.getIsPriceActive());

        verify(productPricingRepository, times(1)).findById(testProductPricing.getId());
        verify(productPricingRepository, times(1)).saveAndFlush(testProductPricing); // Verify save was called with the modified object
    }

    @Test
    void update_shouldThrowRuntimeException_whenProductPricingNotFound() {
        when(productPricingRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productPricingService.update("non-existent-id", newProductPricingRequest));

        assertEquals(DbBash.PRODUCT_PRICING_NOT_FOUND, thrown.getMessage()); // Re-thrown from getProductPricingById

        verify(productPricingRepository, times(1)).findById(anyString());
        verify(productPricingRepository, never()).saveAndFlush(any(ProductPricing.class));
    }

    @Test
    void update_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(productPricingRepository.findById(testProductPricing.getId()))
                .thenReturn(Optional.of(testProductPricing));

        when(productPricingRepository.saveAndFlush(any(ProductPricing.class)))
                .thenThrow(new RuntimeException("DB update error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productPricingService.update(testProductPricing.getId(), newProductPricingRequest));

        assertEquals("DB update error", thrown.getMessage());

        verify(productPricingRepository, times(1)).findById(testProductPricing.getId());
        verify(productPricingRepository, times(1)).saveAndFlush(testProductPricing);
    }

    @Test
    void softDelete_shouldSetIsPriceActiveToFalse_whenSuccessful() {
        when(productPricingRepository.findById(testProductPricing.getId()))
                .thenReturn(Optional.of(testProductPricing)); // Return the original object

        when(productPricingRepository.saveAndFlush(any(ProductPricing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // Return the modified object

        assertDoesNotThrow(() -> productPricingService.softDelete(testProductPricing.getId()));

        assertFalse(testProductPricing.getIsPriceActive());

        verify(productPricingRepository, times(1)).findById(testProductPricing.getId());
        verify(productPricingRepository, times(1)).saveAndFlush(testProductPricing);
    }

    @Test
    void softDelete_shouldThrowRuntimeException_whenProductPricingNotFound() {
        when(productPricingRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productPricingService.softDelete("non-existent-id"));

        assertEquals(DbBash.PRODUCT_PRICING_NOT_FOUND, thrown.getMessage());

        verify(productPricingRepository, times(1)).findById(anyString());
        verify(productPricingRepository, never()).saveAndFlush(any(ProductPricing.class));
    }

    @Test
    void softDelete_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(productPricingRepository.findById(testProductPricing.getId()))
                .thenReturn(Optional.of(testProductPricing));

        when(productPricingRepository.saveAndFlush(any(ProductPricing.class)))
                .thenThrow(new RuntimeException("DB soft delete error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productPricingService.softDelete(testProductPricing.getId()));

        assertEquals("DB soft delete error", thrown.getMessage());

        verify(productPricingRepository, times(1)).findById(testProductPricing.getId());
        verify(productPricingRepository, times(1)).saveAndFlush(testProductPricing);
    }

    @Test
    void toProductPricingResponse_shouldCorrectlyConvertEntityToResponse() {
        ProductPricingResponse response = productPricingService.toProductPricingResponse(testProductPricing);

        assertNotNull(response);
        assertEquals(testProductPricing.getId(), response.getId());
        assertEquals(testProductPricing.getWeekdayPrice(), response.getWeekdayPrice());
        assertEquals(testProductPricing.getWeekendPrice(), response.getWeekendPrice());
        assertEquals(testProductPricing.getIsPriceActive(), response.getIsPriceActive());
        assertEquals(testProductPricing.getPriceDate().toString(), response.getPriceDate());
        assertEquals(testProduct.getId(), response.getProductId());
    }

    @Test
    void toProductPricingResponse_shouldHandleNullProductIdPricing() {
        ProductPricing pricingWithNullProduct = ProductPricing.builder()
                .id("price-null-prod")
                .weekdayPrice(5.0)
                .weekendPrice(7.0)
                .isPriceActive(true)
                .priceDate(LocalDate.now())
                .productIdPricing(null) // Set product to null
                .build();

        ProductPricingResponse response = productPricingService.toProductPricingResponse(pricingWithNullProduct);

        assertNotNull(response);
        assertEquals("price-null-prod", response.getId());
        assertNull(response.getProductId()); // Should be null
    }

    @Test
    void getProductPricingByAttribute_shouldReturnProductPricing_whenFound() {
        when(productService.getProductById(newProductPricingRequest.getProductId())).thenReturn(testProduct);

        when(productPricingRepository.findProductPricingByWeekdayPriceAndWeekendPriceAndIsPriceActiveAndProductIdPricing(
                newProductPricingRequest.getWeekdayPrice(),
                newProductPricingRequest.getWeekendPrice(),
                newProductPricingRequest.getIsPriceActive(),
                testProduct))
                .thenReturn(Optional.of(testProductPricing));

        ProductPricing foundPricing = productPricingService.getProductPricingByAttribute(newProductPricingRequest);

        assertNotNull(foundPricing);
        assertEquals(testProductPricing.getId(), foundPricing.getId());

        verify(productService, times(1)).getProductById(newProductPricingRequest.getProductId());
        verify(productPricingRepository, times(1)).findProductPricingByWeekdayPriceAndWeekendPriceAndIsPriceActiveAndProductIdPricing(
                anyDouble(), anyDouble(), anyBoolean(), any(Product.class));
    }

    @Test
    void getProductPricingByAttribute_shouldReturnNull_whenNotFound() {
        when(productService.getProductById(newProductPricingRequest.getProductId())).thenReturn(testProduct);

        when(productPricingRepository.findProductPricingByWeekdayPriceAndWeekendPriceAndIsPriceActiveAndProductIdPricing(
                anyDouble(), anyDouble(), anyBoolean(), any(Product.class)))
                .thenReturn(Optional.empty());

        ProductPricing foundPricing = productPricingService.getProductPricingByAttribute(newProductPricingRequest);

        assertNull(foundPricing);

        verify(productService, times(1)).getProductById(newProductPricingRequest.getProductId());
        verify(productPricingRepository, times(1)).findProductPricingByWeekdayPriceAndWeekendPriceAndIsPriceActiveAndProductIdPricing(
                anyDouble(), anyDouble(), anyBoolean(), any(Product.class));
    }

    @Test
    void getProductPricingByAttribute_shouldThrowRuntimeException_whenProductServiceFails() {
        when(productService.getProductById(anyString())).thenThrow(new RuntimeException(DbBash.PRODUCT_NOT_FOUND));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productPricingService.getProductPricingByAttribute(newProductPricingRequest));

        assertEquals(DbBash.PRODUCT_NOT_FOUND, thrown.getMessage());

        verify(productService, times(1)).getProductById(anyString());
        verifyNoInteractions(productPricingRepository); // No repository call if product not found
    }
}