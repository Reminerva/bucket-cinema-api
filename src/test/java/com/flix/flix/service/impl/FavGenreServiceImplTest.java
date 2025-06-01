package com.flix.flix.service.impl;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EGenre;
import com.flix.flix.entity.Customer;
import com.flix.flix.entity.FavGenre;
import com.flix.flix.repository.FavGenreRepository;
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
public class FavGenreServiceImplTest {

    @Mock
    private FavGenreRepository favGenreRepository;

    @InjectMocks
    private FavGenreServiceImpl favGenreService;

    private Customer customer1;
    private Customer customer2;
    private FavGenre favGenre1;
    private FavGenre favGenre2;
    private FavGenre favGenreDuplicate;

    @BeforeEach
    void setUp() {
        customer1 = Customer.builder().id("cust-001").fullname("Customer A").build();
        customer2 = Customer.builder().id("cust-002").fullname("Customer B").build();

        favGenre1 = FavGenre.builder()
                .id("fg-001")
                .customer(customer1)
                .favGenre(EGenre.GENRE_ACTION)
                .build();

        favGenre2 = FavGenre.builder()
                .id("fg-002")
                .customer(customer2)
                .favGenre(EGenre.GENRE_COMEDY)
                .build();

        favGenreDuplicate = FavGenre.builder()
                .id("fg-003-dup") // ID will be ignored if duplicate found
                .customer(customer1)
                .favGenre(EGenre.GENRE_ACTION) // Same as favGenre1
                .build();
    }

    @Test
    void create_shouldSaveNewFavGenre_whenNoDuplicateExists() {
        when(favGenreRepository.findAll()).thenReturn(Collections.emptyList());
        when(favGenreRepository.saveAndFlush(any(FavGenre.class))).thenAnswer(invocation -> {
            FavGenre fg = invocation.getArgument(0);
            fg.setId("new-fg-id"); // Simulate ID generation
            return fg;
        });

        FavGenre result = favGenreService.create(favGenre1);

        assertNotNull(result);
        assertEquals("new-fg-id", result.getId());
        assertEquals(favGenre1.getCustomer(), result.getCustomer());
        assertEquals(favGenre1.getFavGenre(), result.getFavGenre());

        verify(favGenreRepository, times(1)).findAll(); // Called to check for duplicates
        verify(favGenreRepository, times(1)).saveAndFlush(favGenre1); // Should be saved

        
    }

    @Test
    void create_shouldReturnExistingFavGenre_whenDuplicateExists() {
        when(favGenreRepository.findAll()).thenReturn(Arrays.asList(favGenre1, favGenre2));

        FavGenre result = favGenreService.create(favGenreDuplicate);

        assertNotNull(result);
        assertEquals(favGenre1.getId(), result.getId()); // Should return the existing favGenre1
        assertEquals(favGenre1.getCustomer(), result.getCustomer());
        assertEquals(favGenre1.getFavGenre(), result.getFavGenre());

        verify(favGenreRepository, times(1)).findAll(); // Called to check for duplicates
        verify(favGenreRepository, never()).saveAndFlush(any(FavGenre.class)); // Should NOT be saved again
    }

    @Test
    void create_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(favGenreRepository.findAll()).thenReturn(Collections.emptyList()); // No duplicate
        when(favGenreRepository.saveAndFlush(any(FavGenre.class))).thenThrow(new RuntimeException("DB error during save"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                favGenreService.create(favGenre1));

        assertTrue(thrown.getMessage().contains("DB error during save"));
        verify(favGenreRepository, times(1)).findAll();
        verify(favGenreRepository, times(1)).saveAndFlush(favGenre1);
    }

    @Test
    void getAll_shouldReturnListOfAllFavGenres() {
        List<FavGenre> expectedFavGenres = Arrays.asList(favGenre1, favGenre2);
        when(favGenreRepository.findAll()).thenReturn(expectedFavGenres);

        List<FavGenre> result = favGenreService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(favGenre1));
        assertTrue(result.contains(favGenre2));
        verify(favGenreRepository, times(1)).findAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoFavGenresExist() {
        when(favGenreRepository.findAll()).thenReturn(Collections.emptyList());

        List<FavGenre> result = favGenreService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(favGenreRepository, times(1)).findAll();
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenRepositoryFindAllFails() {
        when(favGenreRepository.findAll()).thenThrow(new RuntimeException("DB error during findAll"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                favGenreService.getAll());

        assertTrue(thrown.getMessage().contains("DB error during findAll"));
        verify(favGenreRepository, times(1)).findAll();
    }

    @Test
    void getFavGenreById_shouldReturnFavGenre_whenFound() {
        when(favGenreRepository.findById(favGenre1.getId())).thenReturn(Optional.of(favGenre1));

        FavGenre result = favGenreService.getFavGenreById(favGenre1.getId());

        assertNotNull(result);
        assertEquals(favGenre1.getId(), result.getId());
        verify(favGenreRepository, times(1)).findById(favGenre1.getId());
    }

    @Test
    void getFavGenreById_shouldThrowRuntimeException_whenNotFound() {
        when(favGenreRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                favGenreService.getFavGenreById("non-existent-id"));

        assertEquals(DbBash.GENRE_NOT_FOUND, thrown.getMessage());
        verify(favGenreRepository, times(1)).findById(anyString());
    }

    @Test
    void getById_shouldReturnFavGenre_whenFound() {
        when(favGenreRepository.findById(favGenre1.getId())).thenReturn(Optional.of(favGenre1));

        FavGenre result = favGenreService.getById(favGenre1.getId());

        assertNotNull(result);
        assertEquals(favGenre1.getId(), result.getId());
        verify(favGenreRepository, times(1)).findById(favGenre1.getId());
    }

    @Test
    void getById_shouldThrowRuntimeException_whenNotFound() {
        when(favGenreRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                favGenreService.getById("another-non-existent-id"));

        assertEquals(DbBash.GENRE_NOT_FOUND, thrown.getMessage());
        verify(favGenreRepository, times(1)).findById(anyString());
    }

    @Test
    void update_shouldSaveUpdatedFavGenre_whenFound() {
        FavGenre updatedFavGenre = FavGenre.builder()
                .id(favGenre1.getId())
                .customer(customer1)
                .favGenre(EGenre.GENRE_HORROR) // Updated genre
                .build();

        when(favGenreRepository.findById(favGenre1.getId())).thenReturn(Optional.of(favGenre1)); // For getById call
        when(favGenreRepository.saveAndFlush(any(FavGenre.class))).thenReturn(updatedFavGenre);

        FavGenre result = favGenreService.update(favGenre1.getId(), updatedFavGenre);

        assertNotNull(result);
        assertEquals(updatedFavGenre.getId(), result.getId());
        assertEquals(updatedFavGenre.getFavGenre(), result.getFavGenre());

        verify(favGenreRepository, times(1)).findById(favGenre1.getId()); // From getById
        verify(favGenreRepository, times(1)).saveAndFlush(updatedFavGenre);
    }

    @Test
    void update_shouldThrowRuntimeException_whenFavGenreToUpdateNotFound() {
        when(favGenreRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                favGenreService.update("non-existent-id", favGenre1));

        assertEquals(DbBash.GENRE_NOT_FOUND, thrown.getMessage()); // Exception from getById
        verify(favGenreRepository, times(1)).findById(anyString());
        verify(favGenreRepository, never()).saveAndFlush(any(FavGenre.class));
    }

    @Test
    void update_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(favGenreRepository.findById(favGenre1.getId())).thenReturn(Optional.of(favGenre1)); // For getById call
        when(favGenreRepository.saveAndFlush(any(FavGenre.class))).thenThrow(new RuntimeException("DB error during update"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                favGenreService.update(favGenre1.getId(), favGenre1));

        assertTrue(thrown.getMessage().contains("DB error during update"));
        verify(favGenreRepository, times(1)).findById(favGenre1.getId());
        verify(favGenreRepository, times(1)).saveAndFlush(favGenre1);
    }

    @Test
    void delete_shouldDeleteFavGenre_whenFound() {
        when(favGenreRepository.findById(favGenre1.getId())).thenReturn(Optional.of(favGenre1)); // For getById call
        doNothing().when(favGenreRepository).deleteById(favGenre1.getId());

        assertDoesNotThrow(() -> favGenreService.delete(favGenre1.getId()));

        verify(favGenreRepository, times(1)).findById(favGenre1.getId()); // From getById
        verify(favGenreRepository, times(1)).deleteById(favGenre1.getId());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenFavGenreNotFound() {
        when(favGenreRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                favGenreService.delete("non-existent-id"));

        assertEquals(DbBash.GENRE_NOT_FOUND, thrown.getMessage()); // Exception from getById
        verify(favGenreRepository, times(1)).findById(anyString());
        verify(favGenreRepository, never()).deleteById(anyString());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenRepositoryDeleteFails() {
        when(favGenreRepository.findById(favGenre1.getId())).thenReturn(Optional.of(favGenre1)); // For getById call
        doThrow(new RuntimeException("DB error during delete")).when(favGenreRepository).deleteById(favGenre1.getId());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                favGenreService.delete(favGenre1.getId()));

        assertTrue(thrown.getMessage().contains("DB error during delete"));
        verify(favGenreRepository, times(1)).findById(favGenre1.getId());
        verify(favGenreRepository, times(1)).deleteById(favGenre1.getId());
    }
}