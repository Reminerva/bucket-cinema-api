package com.flix.flix.service.impl;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EArtistType;
import com.flix.flix.entity.Artist;
import com.flix.flix.entity.ProductArtist;
import com.flix.flix.model.request.NewArtistRequest;
import com.flix.flix.model.request.search.SearchArtistRequest;
import com.flix.flix.model.response.ArtistResponse;
import com.flix.flix.model.response.ProductArtistResponse;
import com.flix.flix.repository.ArtistRepository;
import com.flix.flix.service.ProductArtistService;
import com.flix.flix.specification.ArtistSpecification;
import com.flix.flix.util.DateUtil;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceImplTest {

    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private ProductArtistService productArtistService;

    @InjectMocks
    private ArtistServiceImpl artistService;

    private Artist testArtist;
    private NewArtistRequest newArtistRequest;

    @BeforeEach
    void setUp() {
        testArtist = Artist.builder()
                .id("art-123")
                .name("John Doe")
                .placeOfBirth("Jakarta")
                .birthDate(LocalDate.of(1990, 1, 1))
                .otherName("JD")
                .bio("A talented artist.")
                .artistTypes(Arrays.asList(EArtistType.TYPE_ACTOR, EArtistType.TYPE_DIRECTOR))
                .productArtists(Collections.emptyList()) // Default empty, can be mocked
                .build();

        newArtistRequest = NewArtistRequest.builder()
                .name("Jane Smith")
                .placeOfBirth("Bandung")
                .birthDate("1985-05-10")
                .otherName("JS")
                .bio("Another talented artist.")
                .artistTypes(List.of("ACTOR", "MUSIC director"))
                .build();
    }

    // --- create Tests ---
    @Test
    void create_shouldReturnArtistResponse_whenSuccessful() {
        // Mock static utility methods
        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {

            mockedDateUtil.when(() -> DateUtil.parseDate(newArtistRequest.getBirthDate()))
                    .thenReturn(LocalDate.of(1985, 5, 10));
            mockedEArtistType.when(() -> EArtistType.toEArtistTypeList(newArtistRequest.getArtistTypes()))
                    .thenReturn(Arrays.asList(EArtistType.TYPE_ACTOR, EArtistType.TYPE_EDITOR));

            when(artistRepository.saveAndFlush(any(Artist.class))).thenAnswer(invocation -> {
                Artist artist = invocation.getArgument(0);
                artist.setId("new-artist-id"); // Simulate ID generation
                return artist;
            });

            ArtistResponse response = artistService.create(newArtistRequest);

            assertNotNull(response);
            assertEquals("new-artist-id", response.getId());
            assertEquals(newArtistRequest.getName(), response.getName());
            assertEquals(newArtistRequest.getBirthDate(), response.getBirthDate());
            assertEquals(2, response.getArtistTypes().size());
            assertTrue(response.getArtistTypes().contains("ACTOR"));
            assertTrue(response.getArtistTypes().contains("MUSIC director"));

            verify(artistRepository, times(1)).saveAndFlush(any(Artist.class));
            mockedDateUtil.verify(() -> DateUtil.parseDate(newArtistRequest.getBirthDate()), times(1));
            mockedEArtistType.verify(() -> EArtistType.toEArtistTypeList(newArtistRequest.getArtistTypes()), times(1));
        }
    }

    @Test
    void create_shouldThrowRuntimeException_whenDuplicateArtistType() {
        newArtistRequest.setArtistTypes(List.of("ACTOR", "ACTOR")); // Duplicate

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                artistService.create(newArtistRequest));

        assertEquals(ApiBash.CREATE_ARTIST_FAILED + ": " + DbBash.DUPLICATE_ARTIST_TYPE_REQUEST, thrown.getMessage());
        verifyNoInteractions(artistRepository); // Should not proceed to save
    }

    @Test
    void create_shouldThrowRuntimeException_whenInvalidBirthDateFormat() {
        newArtistRequest.setBirthDate("invalid-date"); // Invalid format

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(newArtistRequest.getBirthDate()))
                    .thenThrow(new RuntimeException("Invalid date format")); // Simulate parse error

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    artistService.create(newArtistRequest));

            assertTrue(thrown.getMessage().contains(ApiBash.CREATE_ARTIST_FAILED));
            assertTrue(thrown.getMessage().contains("Invalid date format"));
            verifyNoInteractions(artistRepository);
        }
    }

    @Test
    void create_shouldThrowRuntimeException_whenRepositoryThrowsException() {
        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {

            mockedDateUtil.when(() -> DateUtil.parseDate(anyString())).thenReturn(LocalDate.now());
            mockedEArtistType.when(() -> EArtistType.toEArtistTypeList(anyList())).thenReturn(Collections.emptyList());

            // Simulate a database error
            doThrow(new RuntimeException("DB Error")).when(artistRepository).saveAndFlush(any(Artist.class));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    artistService.create(newArtistRequest));

            assertTrue(thrown.getMessage().contains(ApiBash.CREATE_ARTIST_FAILED));
            assertTrue(thrown.getMessage().contains("DB Error"));
            verify(artistRepository, times(1)).saveAndFlush(any(Artist.class));
        }
    }

    // --- getAll Tests ---
    @Test
    void getAll_shouldReturnPageOfArtistResponses_withDefaultPagingAndSorting() {
        SearchArtistRequest searchRequest = SearchArtistRequest.builder()
                .page(0) // Will be converted to 1
                .size(0) // Will be converted to 10
                .birthDateMax("2000-01-01")
                .birthDateMin("1990-01-01")
                .sortBy("name")
                .direction("asc")
                .build();

        List<Artist> artists = Collections.singletonList(testArtist);
        Page<Artist> artistPage = new PageImpl<>(artists, PageRequest.of(0, 10, Sort.by("name").ascending()), 1);

        try (MockedStatic<ArtistSpecification> mockedStaticSpec = mockStatic(ArtistSpecification.class);
                MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {

            mockedStaticSpec.when(() -> ArtistSpecification.getSpecification(searchRequest)).thenReturn(mock(Specification.class));
            mockedEArtistType.when(() -> EArtistType.toEArtistTypeStringList(anyList())).thenReturn(Arrays.asList("ACTOR", "DIRECTOR"));

            when(artistRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(artistPage);

            Page<ArtistResponse> result = artistService.getAll(searchRequest);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testArtist.getName(), result.getContent().get(0).getName());

            verify(artistRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedStaticSpec.verify(() -> ArtistSpecification.getSpecification(searchRequest), times(1));
            mockedEArtistType.verify(() -> EArtistType.toEArtistTypeStringList(anyList()), times(1));
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenMinDateAfterMaxDate() {
        SearchArtistRequest searchRequest = SearchArtistRequest.builder()
                .page(0) // Will be converted to 1
                .size(0) // Will be converted to 10
                .sortBy("name")
                .direction("asc")
                .birthDateMin("2000-01-01")
                .birthDateMax("1990-01-01") // Min after Max
                .build();

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.parseDate(searchRequest.getBirthDateMin())).thenReturn(LocalDate.of(2000, 1, 1));
            mockedDateUtil.when(() -> DateUtil.parseDate(searchRequest.getBirthDateMax())).thenReturn(LocalDate.of(1990, 1, 1));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    artistService.getAll(searchRequest));

            assertEquals(ApiBash.GET_ALL_ARTIST_FAILED + ": " + DbBash.MIN_MAX_INVALID, thrown.getMessage());
            verifyNoInteractions(artistRepository);
        }
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenRepositoryThrowsException() {
        SearchArtistRequest searchRequest = SearchArtistRequest.builder().page(1).size(10).sortBy("name").direction("asc").build();

        try (MockedStatic<ArtistSpecification> mockedStaticSpec = mockStatic(ArtistSpecification.class);
             MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) { // Include EArtistType mock
            
            mockedStaticSpec.when(() -> ArtistSpecification.getSpecification(searchRequest)).thenReturn(mock(Specification.class));
            
            // Mock to prevent NPE if map(this::toArtistResponse) is called
            mockedEArtistType.when(() -> EArtistType.toEArtistTypeStringList(anyList())).thenReturn(Collections.emptyList());

            doThrow(new RuntimeException("DB access error")).when(artistRepository).findAll(any(Specification.class), any(Pageable.class));

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    artistService.getAll(searchRequest));

            assertTrue(thrown.getMessage().contains(ApiBash.GET_ALL_ARTIST_FAILED));
            assertTrue(thrown.getMessage().contains("DB access error"));
            verify(artistRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            mockedStaticSpec.verify(() -> ArtistSpecification.getSpecification(searchRequest), times(1));
        }
    }


    // --- getById Tests ---
    @Test
    void getById_shouldReturnArtistResponse_whenFound() {
        try (MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {
            mockedEArtistType.when(() -> EArtistType.toEArtistTypeStringList(anyList())).thenReturn(Arrays.asList("ACTOR", "DIRECTOR"));

            when(artistRepository.findById(testArtist.getId())).thenReturn(Optional.of(testArtist));

            ArtistResponse response = artistService.getById(testArtist.getId());

            assertNotNull(response);
            assertEquals(testArtist.getId(), response.getId());
            assertEquals(testArtist.getName(), response.getName());
            assertEquals(2, response.getArtistTypes().size());
            verify(artistRepository, times(1)).findById(testArtist.getId());
            mockedEArtistType.verify(() -> EArtistType.toEArtistTypeStringList(anyList()), times(1));
        }
    }

    @Test
    void getById_shouldThrowRuntimeException_whenNotFound() {
        when(artistRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                artistService.getById("non-existent-id"));

        assertEquals(ApiBash.GET_ARTIST_FAILED + ": " + DbBash.ARTIST_NOT_FOUND, thrown.getMessage());
        verify(artistRepository, times(1)).findById(anyString());
    }

    // --- getArtistById Tests ---
    @Test
    void getArtistById_shouldReturnArtist_whenFound() {
        when(artistRepository.findById(testArtist.getId())).thenReturn(Optional.of(testArtist));

        Artist foundArtist = artistService.getArtistById(testArtist.getId());

        assertNotNull(foundArtist);
        assertEquals(testArtist.getId(), foundArtist.getId());
        verify(artistRepository, times(1)).findById(testArtist.getId());
    }

    @Test
    void getArtistById_shouldThrowRuntimeException_whenNotFound() {
        when(artistRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                artistService.getArtistById("non-existent-id"));

        assertEquals(DbBash.ARTIST_NOT_FOUND, thrown.getMessage());
        verify(artistRepository, times(1)).findById(anyString());
    }

    // --- update Tests ---
    @Test
    void update_shouldReturnArtistResponse_whenSuccessful() {
        NewArtistRequest updatedRequest = NewArtistRequest.builder()
                .name("Updated Name")
                .placeOfBirth("New City")
                .birthDate("1995-10-20")
                .otherName("New Alias")
                .bio("Updated bio.")
                .artistTypes(List.of("WRITER", "DIRECTOR"))
                .build();

        Artist existingArtist = Artist.builder()
                .id(testArtist.getId())
                .name("Old Name")
                .placeOfBirth("Old City")
                .birthDate(LocalDate.of(1980, 1, 1))
                .otherName("Old Alias")
                .bio("Old bio.")
                .artistTypes(Arrays.asList(EArtistType.TYPE_ACTOR))
                .build();

        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class);
                MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {

            mockedDateUtil.when(() -> DateUtil.parseDate(updatedRequest.getBirthDate()))
                    .thenReturn(LocalDate.of(1995, 10, 20));
            mockedEArtistType.when(() -> EArtistType.toEArtistTypeList(updatedRequest.getArtistTypes()))
                    .thenReturn(Arrays.asList(EArtistType.TYPE_WRITER, EArtistType.TYPE_DIRECTOR));
            mockedEArtistType.when(() -> EArtistType.toEArtistTypeStringList(anyList()))
                    .thenReturn(Arrays.asList("WRITER", "DIRECTOR")); // For toArtistResponse

            when(artistRepository.findById(testArtist.getId())).thenReturn(Optional.of(existingArtist));
            when(artistRepository.saveAndFlush(any(Artist.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ArtistResponse response = artistService.update(testArtist.getId(), updatedRequest);

            assertNotNull(response);
            assertEquals(testArtist.getId(), response.getId());
            assertEquals(updatedRequest.getName(), response.getName());
            assertEquals(updatedRequest.getBirthDate(), response.getBirthDate());
            assertEquals(2, response.getArtistTypes().size());
            assertTrue(response.getArtistTypes().contains("WRITER"));
            assertTrue(response.getArtistTypes().contains("DIRECTOR"));

            verify(artistRepository, times(1)).findById(testArtist.getId());
            verify(artistRepository, times(1)).saveAndFlush(any(Artist.class));
            mockedDateUtil.verify(() -> DateUtil.parseDate(updatedRequest.getBirthDate()), times(1));
            mockedEArtistType.verify(() -> EArtistType.toEArtistTypeList(updatedRequest.getArtistTypes()), times(1));
            mockedEArtistType.verify(() -> EArtistType.toEArtistTypeStringList(anyList()), times(1));
        }
    }

    @Test
    void update_shouldThrowRuntimeException_whenArtistNotFound() {
        when(artistRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                artistService.update("non-existent-id", newArtistRequest));

        assertEquals(ApiBash.UPDATE_ARTIST_FAILED + ": " + DbBash.ARTIST_NOT_FOUND, thrown.getMessage());
        verify(artistRepository, times(1)).findById(anyString());
        verify(artistRepository, never()).saveAndFlush(any(Artist.class));
    }

    @Test
    void update_shouldThrowRuntimeException_whenDuplicateArtistType() {
        newArtistRequest.setArtistTypes(List.of("ACTOR", "ACTOR")); // Duplicate

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                artistService.update(testArtist.getId(), newArtistRequest));

        assertEquals(ApiBash.UPDATE_ARTIST_FAILED + ": " + DbBash.DUPLICATE_ARTIST_TYPE_REQUEST, thrown.getMessage());
        verify(artistRepository, never()).findById(anyString()); // Validate happens before findById
    }


    // --- delete Tests ---
    @Test
    void delete_shouldDeleteArtist_whenFound() {
        when(artistRepository.findById(testArtist.getId())).thenReturn(Optional.of(testArtist));
        doNothing().when(artistRepository).deleteById(testArtist.getId());

        assertDoesNotThrow(() -> artistService.delete(testArtist.getId()));

        verify(artistRepository, times(1)).findById(testArtist.getId());
        verify(artistRepository, times(1)).deleteById(testArtist.getId());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenNotFound() {
        when(artistRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                artistService.delete("non-existent-id"));

        assertEquals(ApiBash.DELETE_ARTIST_FAILED + ": " + DbBash.ARTIST_NOT_FOUND, thrown.getMessage());
        verify(artistRepository, times(1)).findById(anyString());
        verify(artistRepository, never()).deleteById(anyString());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenRepositoryThrowsException() {
        when(artistRepository.findById(testArtist.getId())).thenReturn(Optional.of(testArtist));
        doThrow(new RuntimeException("DB delete error")).when(artistRepository).deleteById(testArtist.getId());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                artistService.delete(testArtist.getId()));

        assertTrue(thrown.getMessage().contains(ApiBash.DELETE_ARTIST_FAILED));
        assertTrue(thrown.getMessage().contains("DB delete error"));
        verify(artistRepository, times(1)).findById(testArtist.getId());
        verify(artistRepository, times(1)).deleteById(testArtist.getId());
    }

    // --- toArtistResponse related tests (implicit and explicit) ---
    @Test
    void toArtistResponse_shouldHandleNullBirthDate() {
        Artist artistWithNullDate = Artist.builder().id("id").name("name").birthDate(null).build();
        try (MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {
            mockedEArtistType.when(() -> EArtistType.toEArtistTypeStringList(anyList())).thenReturn(Collections.emptyList());

            // Call via getById for simplicity, ensures toArtistResponse is called
            when(artistRepository.findById(artistWithNullDate.getId())).thenReturn(Optional.of(artistWithNullDate));
            ArtistResponse response = artistService.getById(artistWithNullDate.getId());
            assertNull(response.getBirthDate());
        }
    }

    @Test
    void toArtistResponse_shouldHandleNullArtistTypes() {
        Artist artistWithNullTypes = Artist.builder().id("id").name("name").artistTypes(null).build();
        
        // Call via getById for simplicity, ensures toArtistResponse is called
        when(artistRepository.findById(artistWithNullTypes.getId())).thenReturn(Optional.of(artistWithNullTypes));
        ArtistResponse response = artistService.getById(artistWithNullTypes.getId());
        assertNull(response.getArtistTypes());
        // No need to mock EArtistType.toEArtistTypeStringList because it won't be called if artistTypes is null
    }

    @Test
    void toArtistResponse_shouldHandleProductArtists() {
        ProductArtist pa1 = mock(ProductArtist.class);
        ProductArtist pa2 = mock(ProductArtist.class);
        List<ProductArtist> productArtists = Arrays.asList(pa1, pa2);
        
        ProductArtistResponse paResponse1 = ProductArtistResponse.builder().id("pa1").build();
        ProductArtistResponse paResponse2 = ProductArtistResponse.builder().id("pa2").build();

        Artist artistWithProductArtists = Artist.builder()
                .id("art-prod")
                .name("Artist with Products")
                .productArtists(productArtists)
                .build();

        try (MockedStatic<EArtistType> mockedEArtistType = mockStatic(EArtistType.class)) {
            mockedEArtistType.when(() -> EArtistType.toEArtistTypeStringList(anyList())).thenReturn(Collections.emptyList());
            when(productArtistService.toProductArtistResponse(pa1)).thenReturn(paResponse1);
            when(productArtistService.toProductArtistResponse(pa2)).thenReturn(paResponse2);

            when(artistRepository.findById(artistWithProductArtists.getId())).thenReturn(Optional.of(artistWithProductArtists));
            ArtistResponse response = artistService.getById(artistWithProductArtists.getId());

            assertNotNull(response.getProductArtists());
            assertEquals(2, response.getProductArtists().size());
            assertEquals("pa1", response.getProductArtists().get(0).getId());
            assertEquals("pa2", response.getProductArtists().get(1).getId());

            verify(productArtistService, times(1)).toProductArtistResponse(pa1);
            verify(productArtistService, times(1)).toProductArtistResponse(pa2);
        }
    }

    // --- validateArtistRequest tests (implicit) ---
    // Test for this is covered in create_shouldThrowRuntimeException_whenDuplicateArtistType
    // and update_shouldThrowRuntimeException_whenDuplicateArtistType
}