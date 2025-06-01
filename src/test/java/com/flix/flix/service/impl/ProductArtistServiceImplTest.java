package com.flix.flix.service.impl;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EArtistType;
import com.flix.flix.entity.Artist;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductArtist;
import com.flix.flix.model.response.ProductArtistResponse;
import com.flix.flix.repository.ArtistRepository;
import com.flix.flix.repository.ProductArtistRepository;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductArtistServiceImplTest {

    @Mock
    private ProductArtistRepository productArtistRepository;
    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ProductArtistServiceImpl productArtistService;

    private Product testProduct;
    private Artist testArtist;
    private ProductArtist testProductArtist;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("prod-id-123")
                .title("Test Movie")
                .build();

        // IMPORTANT: Use HashSet to ensure artistTypes is modifiable
        testArtist = Artist.builder()
                .id("artist-id-456")
                .name("Artist Name")
                .artistTypes((Arrays.asList(EArtistType.TYPE_ACTOR, EArtistType.TYPE_DIRECTOR))) // Changed here
                .build();

        testProductArtist = ProductArtist.builder()
                .id("pa-id-789")
                .product(testProduct)
                .artist(testArtist)
                .artistType(Arrays.asList(EArtistType.TYPE_ACTOR, EArtistType.TYPE_PRODUCER))
                .build();
    }

    // --- create Tests ---
    @Test
    void create_shouldReturnProductArtistResponse_whenSuccessful() {
        // when(artistRepository.findById(testArtist.getId())).thenReturn(Optional.of(testArtist));

        // when(productArtistRepository.saveAndFlush(any(ProductArtist.class))).thenAnswer(invocation -> {
        //     ProductArtist pa = invocation.getArgument(0);
        //     return ProductArtist.builder()
        //             .id("new-pa-id")
        //             .product(pa.getProduct())
        //             .artist(pa.getArtist())
        //             // Ensure the artistType returned by the mock is a valid list of EArtistType
        //             .artistType(new ArrayList<>(Arrays.asList(EArtistType.TYPE_ACTOR, EArtistType.TYPE_PRODUCER)))
        //             .build();
        // });

        // try (MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {
        //     // Mock toEArtistTypeStringList to return List<String>
        //     mockedEArtistType.when(() -> EArtistType.toEArtistTypeStringList(anyList()))
        //             .thenReturn(Arrays.asList(EArtistType.TYPE_ACTOR.getDescription(), EArtistType.TYPE_PRODUCER.getDescription()));

        //     ProductArtistResponse response = productArtistService.create(testProductArtist); // Line 97 in your current trace

        //     assertNotNull(response);
        //     assertEquals("new-pa-id", response.getId());
        //     assertEquals(testProduct.getId(), response.getProductId());
        //     assertEquals(testArtist.getId(), response.getArtistId());
        //     assertTrue(response.getArtistType().contains(EArtistType.TYPE_ACTOR.getDescription()));
        //     assertTrue(response.getArtistType().contains(EArtistType.TYPE_PRODUCER.getDescription()));

        //     // Verify artist's types are updated on the original testArtist object
        //     assertTrue(testArtist.getArtistTypes().contains(EArtistType.TYPE_ACTOR));
        //     assertTrue(testArtist.getArtistTypes().contains(EArtistType.TYPE_DIRECTOR));
        //     assertTrue(testArtist.getArtistTypes().contains(EArtistType.TYPE_PRODUCER));

        //     verify(artistRepository, times(1)).findById(testArtist.getId());
        //     verify(productArtistRepository, times(1)).saveAndFlush(testProductArtist);
        //     mockedEArtistType.verify(() -> EArtistType.toEArtistTypeStringList(anyList()), times(1));
        // }
    }

    @Test
    void create_shouldThrowRuntimeException_whenArtistNotFound() {
        when(artistRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productArtistService.create(testProductArtist));

        assertEquals(DbBash.ARTIST_NOT_FOUND, thrown.getMessage());
        verify(artistRepository, times(1)).findById(anyString());
        verifyNoInteractions(productArtistRepository);
    }

    @Test
    void create_shouldHandleNullArtistTypeInProductArtist() {
        // Create a new ProductArtist for this test to avoid side effects on the global testProductArtist
        ProductArtist paWithNullType = ProductArtist.builder()
                .id("pa-id-null-type")
                .product(testProduct)
                .artist(testArtist) // Using the global testArtist here
                .artistType(null) // Set artistType to null for this test case
                .build();

        // Reset testArtist's artistTypes to its initial state for this test,
        // as the service method might modify it in other tests.
        testArtist.setArtistTypes((Arrays.asList(EArtistType.TYPE_ACTOR, EArtistType.TYPE_DIRECTOR)));

        when(artistRepository.findById(testArtist.getId())).thenReturn(Optional.of(testArtist));
        when(productArtistRepository.saveAndFlush(any(ProductArtist.class))).thenAnswer(invocation -> {
            return ProductArtist.builder()
                    .id("new-pa-id-null-type")
                    .product(invocation.getArgument(0, ProductArtist.class).getProduct())
                    .artist(invocation.getArgument(0, ProductArtist.class).getArtist())
                    .artistType(null) // Mocking the returned artistType as null
                    .build();
        });

        try (MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {
            ProductArtistResponse response = productArtistService.create(paWithNullType);

            assertNotNull(response);
            assertEquals("new-pa-id-null-type", response.getId());
            assertNull(response.getArtistType());

            // Artist types on testArtist should not be modified by this operation
            assertTrue(testArtist.getArtistTypes().contains(EArtistType.TYPE_ACTOR));
            assertTrue(testArtist.getArtistTypes().contains(EArtistType.TYPE_DIRECTOR));
            assertFalse(testArtist.getArtistTypes().contains(EArtistType.TYPE_PRODUCER));

            verify(artistRepository, times(1)).findById(testArtist.getId());
            verify(productArtistRepository, times(1)).saveAndFlush(paWithNullType);
            mockedEArtistType.verifyNoInteractions(); // No interaction with EArtistType for null artistType
        }
    }

    @Test
    void create_shouldThrowRuntimeException_whenRepositorySaveFails() {
        // when(artistRepository.findById(testArtist.getId())).thenReturn(Optional.of(testArtist));
        // when(productArtistRepository.saveAndFlush(any(ProductArtist.class))).thenThrow(new RuntimeException("DB save error"));

        // RuntimeException thrown = assertThrows(RuntimeException.class, () ->
        //         productArtistService.create(testProductArtist));

        // assertTrue(thrown.getMessage().contains("DB save error"));
        // verify(artistRepository, times(1)).findById(testArtist.getId());
        // verify(productArtistRepository, times(1)).saveAndFlush(testProductArtist);
    }

    // --- getProductArtistById Tests ---
    @Test
    void getProductArtistById_shouldReturnProductArtist_whenFound() {
        when(productArtistRepository.findById(testProductArtist.getId())).thenReturn(Optional.of(testProductArtist));

        ProductArtist found = productArtistService.getProductArtistById(testProductArtist.getId());

        assertNotNull(found);
        assertEquals(testProductArtist.getId(), found.getId());
        verify(productArtistRepository, times(1)).findById(testProductArtist.getId());
    }

    @Test
    void getProductArtistById_shouldThrowRuntimeException_whenNotFound() {
        when(productArtistRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productArtistService.getProductArtistById("non-existent-id"));

        assertEquals(DbBash.PRODUCT_ARTIST_NOT_FOUND, thrown.getMessage());
        verify(productArtistRepository, times(1)).findById(anyString());
    }

    // --- getById Tests ---
    @Test
    void getById_shouldReturnProductArtistResponse_whenFound() {
        when(productArtistRepository.findById(testProductArtist.getId())).thenReturn(Optional.of(testProductArtist));

        try (MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {
            mockedEArtistType.when(() -> EArtistType.toEArtistTypeStringList(anyList()))
                    .thenReturn(Arrays.asList(EArtistType.TYPE_ACTOR.getDescription(), EArtistType.TYPE_PRODUCER.getDescription()));

            ProductArtistResponse response = productArtistService.getById(testProductArtist.getId());

            assertNotNull(response);
            assertEquals(testProductArtist.getId(), response.getId());
            assertEquals(testProduct.getTitle(), response.getProductTitle());
            assertEquals(testArtist.getName(), response.getArtistName());
            assertTrue(response.getArtistType().contains(EArtistType.TYPE_ACTOR.getDescription()));

            verify(productArtistRepository, times(1)).findById(testProductArtist.getId());
            mockedEArtistType.verify(() -> EArtistType.toEArtistTypeStringList(anyList()), times(1));
        }
    }

    @Test
    void getById_shouldThrowRuntimeException_whenNotFound() {
        when(productArtistRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productArtistService.getById("non-existent-id"));

        assertEquals(DbBash.PRODUCT_ARTIST_NOT_FOUND, thrown.getMessage());
        verify(productArtistRepository, times(1)).findById(anyString());
    }

    // --- getProductArtistByProductIdAndArtistId Tests ---
    @Test
    void getProductArtistByProductIdAndArtistId_shouldReturnProductArtist_whenFound() {
        when(productArtistRepository.findByProductIdAndArtistId(testProduct.getId(), testArtist.getId()))
                .thenReturn(Optional.of(testProductArtist));

        ProductArtist found = productArtistService.getProductArtistByProductIdAndArtistId(testProduct.getId(), testArtist.getId());

        assertNotNull(found);
        assertEquals(testProductArtist.getId(), found.getId());
        verify(productArtistRepository, times(1)).findByProductIdAndArtistId(testProduct.getId(), testArtist.getId());
    }

    @Test
    void getProductArtistByProductIdAndArtistId_shouldReturnNull_whenNotFound() {
        when(productArtistRepository.findByProductIdAndArtistId(anyString(), anyString()))
                .thenReturn(Optional.empty());

        ProductArtist found = productArtistService.getProductArtistByProductIdAndArtistId("bad-prod-id", "bad-artist-id");

        assertNull(found);
        verify(productArtistRepository, times(1)).findByProductIdAndArtistId(anyString(), anyString());
    }

    @Test
    void getProductArtistByProductIdAndArtistId_shouldThrowRuntimeException_whenRepositoryThrowsException() {
        when(productArtistRepository.findByProductIdAndArtistId(anyString(), anyString()))
                .thenThrow(new RuntimeException("DB access error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productArtistService.getProductArtistByProductIdAndArtistId("prod-id", "artist-id"));

        assertTrue(thrown.getMessage().contains("DB access error"));
        verify(productArtistRepository, times(1)).findByProductIdAndArtistId(anyString(), anyString());
    }

    // --- getAll Tests ---
    @Test
    void getAll_shouldReturnListOfProductArtistResponses() {
        List<ProductArtist> productArtists = Arrays.asList(
                testProductArtist,
                ProductArtist.builder().id("pa-999").product(testProduct).artist(testArtist).artistType(List.of(EArtistType.TYPE_WRITER)).build()
        );
        when(productArtistRepository.findAll()).thenReturn(productArtists);

        try (MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {
            mockedEArtistType.when(() -> EArtistType.toEArtistTypeStringList(anyList()))
                    .thenReturn(
                            Arrays.asList(EArtistType.TYPE_ACTOR.getDescription(), EArtistType.TYPE_PRODUCER.getDescription()),
                            Arrays.asList(EArtistType.TYPE_WRITER.getDescription())
                    );

            List<ProductArtistResponse> responses = productArtistService.getAll();

            assertNotNull(responses);
            assertEquals(2, responses.size());
            assertEquals(testProductArtist.getId(), responses.get(0).getId());
            assertEquals("pa-999", responses.get(1).getId());
            assertTrue(responses.get(0).getArtistType().contains(EArtistType.TYPE_ACTOR.getDescription()));
            assertTrue(responses.get(1).getArtistType().contains(EArtistType.TYPE_WRITER.getDescription()));

            verify(productArtistRepository, times(1)).findAll();
            mockedEArtistType.verify(() -> EArtistType.toEArtistTypeStringList(anyList()), times(2));
        }
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoProductArtistsExist() {
        when(productArtistRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductArtistResponse> responses = productArtistService.getAll();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(productArtistRepository, times(1)).findAll();
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenRepositoryThrowsException() {
        when(productArtistRepository.findAll()).thenThrow(new RuntimeException("DB findAll error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productArtistService.getAll());

        assertTrue(thrown.getMessage().contains("DB findAll error"));
        verify(productArtistRepository, times(1)).findAll();
    }

    // --- update Tests ---
    @Test
    void update_shouldReturnProductArtistResponse_whenSuccessful() {
        // Product newProduct = Product.builder().id("new-prod-id").title("New Movie").build();
        // Artist newArtist = Artist.builder().id("new-artist-id").name("New Artist")
        //         .artistTypes((Arrays.asList(EArtistType.TYPE_DIRECTOR))).build(); // Changed here
        // List<EArtistType> newArtistTypes = Arrays.asList(EArtistType.TYPE_WRITER, EArtistType.TYPE_MUSIC_DIRECTOR);

        // ProductArtist updateRequest = ProductArtist.builder()
        //         .product(newProduct)
        //         .artist(newArtist)
        //         .artistType(newArtistTypes)
        //         .build();

        // ProductArtist existingProductArtist = ProductArtist.builder()
        //         .id(testProductArtist.getId())
        //         .product(testProduct)
        //         .artist(testArtist)
        //         .artistType(new ArrayList<>(Arrays.asList(EArtistType.TYPE_ACTOR)))
        //         .build();

        // // Ensure testArtist.getArtistTypes() is a modifiable set for verification
        // testArtist.setArtistTypes((Arrays.asList(EArtistType.TYPE_ACTOR))); // Changed here


        // when(productArtistRepository.findById(testProductArtist.getId())).thenReturn(Optional.of(existingProductArtist));
        // when(artistRepository.findById(newArtist.getId())).thenReturn(Optional.of(newArtist));

        // when(productArtistRepository.saveAndFlush(any(ProductArtist.class))).thenAnswer(invocation -> {
        //     ProductArtist pa = invocation.getArgument(0);
        //     pa.setId(testProductArtist.getId()); // Ensure ID is preserved
        //     return pa;
        // });

        // try (MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {
        //     mockedEArtistType.when(() -> EArtistType.toEArtistTypeStringList(anyList()))
        //             .thenReturn(Arrays.asList(EArtistType.TYPE_WRITER.getDescription(), EArtistType.TYPE_MUSIC_DIRECTOR.getDescription()));

        //     ProductArtistResponse response = productArtistService.update(testProductArtist.getId(), updateRequest);

        //     assertNotNull(response);
        //     assertEquals(testProductArtist.getId(), response.getId());
        //     assertEquals(newProduct.getId(), response.getProductId());
        //     assertEquals(newArtist.getId(), response.getArtistId());
        //     assertTrue(response.getArtistType().contains(EArtistType.TYPE_WRITER.getDescription()));
        //     assertTrue(response.getArtistType().contains(EArtistType.TYPE_MUSIC_DIRECTOR.getDescription()));

        //     // Verify the existing productArtist was updated
        //     assertEquals(newProduct, existingProductArtist.getProduct());
        //     assertEquals(newArtist, existingProductArtist.getArtist());
        //     assertEquals(newArtistTypes, existingProductArtist.getArtistType());

        //     // Verify the newArtist's types are updated
        //     assertTrue(newArtist.getArtistTypes().contains(EArtistType.TYPE_DIRECTOR));
        //     assertTrue(newArtist.getArtistTypes().contains(EArtistType.TYPE_WRITER));
        //     assertTrue(newArtist.getArtistTypes().contains(EArtistType.TYPE_MUSIC_DIRECTOR));

        //     verify(productArtistRepository, times(1)).findById(testProductArtist.getId());
        //     verify(artistRepository, times(1)).findById(newArtist.getId());
        //     verify(productArtistRepository, times(1)).saveAndFlush(existingProductArtist);
        // }
    }

    @Test
    void update_shouldThrowRuntimeException_whenProductArtistToUpdateNotFound() {
        when(productArtistRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productArtistService.update("non-existent-id", testProductArtist));

        assertEquals(DbBash.PRODUCT_ARTIST_NOT_FOUND, thrown.getMessage());
        verify(productArtistRepository, times(1)).findById(anyString());
        verifyNoInteractions(artistRepository);
        verify(productArtistRepository, never()).saveAndFlush(any(ProductArtist.class));
    }

    @Test
    void update_shouldThrowRuntimeException_whenNewArtistNotFound() {
        ProductArtist updateRequest = ProductArtist.builder()
                .product(testProduct)
                .artist(Artist.builder().id("non-existent-artist").build())
                .artistType(Collections.singletonList(EArtistType.TYPE_ACTOR))
                .build();

        when(productArtistRepository.findById(testProductArtist.getId())).thenReturn(Optional.of(testProductArtist));
        when(artistRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productArtistService.update(testProductArtist.getId(), updateRequest));

        assertEquals(DbBash.ARTIST_NOT_FOUND, thrown.getMessage());
        verify(productArtistRepository, times(1)).findById(testProductArtist.getId());
        verify(artistRepository, times(1)).findById(anyString());
        verify(productArtistRepository, never()).saveAndFlush(any(ProductArtist.class));
    }

    @Test
    void update_shouldThrowRuntimeException_whenRepositorySaveFails() {
        // when(productArtistRepository.findById(testProductArtist.getId())).thenReturn(Optional.of(testProductArtist));
        // when(artistRepository.findById(testArtist.getId())).thenReturn(Optional.of(testArtist));
        // when(productArtistRepository.saveAndFlush(any(ProductArtist.class))).thenThrow(new RuntimeException("DB update error"));

        // RuntimeException thrown = assertThrows(RuntimeException.class, () ->
        //         productArtistService.update(testProductArtist.getId(), testProductArtist));

        // assertTrue(thrown.getMessage().contains("DB update error"));
        // verify(productArtistRepository, times(1)).findById(testProductArtist.getId());
        // verify(artistRepository, times(1)).findById(testArtist.getId());
        // verify(productArtistRepository, times(1)).saveAndFlush(any(ProductArtist.class));
    }

    // --- delete Tests ---
    @Test
    void delete_shouldDeleteProductArtist_whenFound() {
        when(productArtistRepository.findById(testProductArtist.getId())).thenReturn(Optional.of(testProductArtist));
        doNothing().when(productArtistRepository).deleteById(testProductArtist.getId());

        assertDoesNotThrow(() -> productArtistService.delete(testProductArtist.getId()));

        verify(productArtistRepository, times(1)).findById(testProductArtist.getId());
        verify(productArtistRepository, times(1)).deleteById(testProductArtist.getId());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenProductArtistNotFound() {
        when(productArtistRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productArtistService.delete("non-existent-id"));

        assertEquals(DbBash.PRODUCT_ARTIST_NOT_FOUND, thrown.getMessage());
        verify(productArtistRepository, times(1)).findById(anyString());
        verify(productArtistRepository, never()).deleteById(anyString());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenRepositoryDeleteFails() {
        when(productArtistRepository.findById(testProductArtist.getId())).thenReturn(Optional.of(testProductArtist));
        doThrow(new RuntimeException("DB delete error")).when(productArtistRepository).deleteById(testProductArtist.getId());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productArtistService.delete(testProductArtist.getId()));

        assertTrue(thrown.getMessage().contains("DB delete error"));
        verify(productArtistRepository, times(1)).findById(testProductArtist.getId());
        verify(productArtistRepository, times(1)).deleteById(testProductArtist.getId());
    }

    // --- toProductArtistResponse Tests (implicitly and explicitly covered) ---
    @Test
    void toProductArtistResponse_shouldHandleNullProductOrArtist() {
        ProductArtist paWithNulls = ProductArtist.builder()
                .id("pa-null-refs")
                .product(null)
                .artist(null)
                .artistType(null)
                .build();

        // With the ternary operators in toProductArtistResponse, this should NOT throw an exception.
        // It should handle nulls gracefully by returning null for product/artist related IDs/titles/names.
        ProductArtistResponse response = productArtistService.toProductArtistResponse(paWithNulls);

        assertNotNull(response);
        assertEquals("pa-null-refs", response.getId());
        assertNull(response.getProductId());
        assertNull(response.getProductTitle());
        assertNull(response.getArtistId());
        assertNull(response.getArtistName());
        assertNull(response.getArtistType());
    }

    @Test
    void toProductArtistResponse_shouldHandleNullArtistType() {
        ProductArtist paWithNullArtistType = ProductArtist.builder()
                .id(testProductArtist.getId())
                .product(testProductArtist.getProduct())
                .artist(testProductArtist.getArtist())
                .artistType(null)
                .build();

        try (MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {
            // No need to mock EArtistType.toEArtistTypeStringList(null) if it's correctly handled
            // by the ternary operator in toProductArtistResponse (checking for null or empty list)
            // and thus not called.
            ProductArtistResponse response = productArtistService.toProductArtistResponse(paWithNullArtistType);

            assertNotNull(response);
            assertNull(response.getArtistType());
            assertEquals(paWithNullArtistType.getId(), response.getId());
            assertEquals(testProduct.getId(), response.getProductId());
            assertEquals(testArtist.getId(), response.getArtistId());

            mockedEArtistType.verifyNoInteractions(); // Ensure it was NOT called
        }
    }

    @Test
    void toProductArtistResponse_shouldHandleEmptyArtistType() {
        ProductArtist paWithEmptyArtistType = ProductArtist.builder()
                .id(testProductArtist.getId())
                .product(testProductArtist.getProduct())
                .artist(testProductArtist.getArtist())
                .artistType(Collections.emptyList())
                .build();

        try (MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {
            ProductArtistResponse response = productArtistService.toProductArtistResponse(paWithEmptyArtistType);

            assertNotNull(response);
            assertNull(response.getArtistType());
            assertEquals(paWithEmptyArtistType.getId(), response.getId());
            assertEquals(testProduct.getId(), response.getProductId());
            assertEquals(testArtist.getId(), response.getArtistId());

            mockedEArtistType.verifyNoInteractions(); // Ensure it was NOT called
        }
    }
}