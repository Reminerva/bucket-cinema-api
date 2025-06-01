package com.flix.flix.service.impl;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EGenre;
import com.flix.flix.entity.MovieGenre;
import com.flix.flix.entity.Product; // Asumsi ada kelas Product
import com.flix.flix.repository.MovieGenreRepository;
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
public class MovieGenreServiceImplTest {

    @Mock
    private MovieGenreRepository movieGenreRepository;

    @InjectMocks
    private MovieGenreServiceImpl movieGenreService;

    private Product product1;
    private Product product2;
    private MovieGenre movieGenre1;
    private MovieGenre movieGenre2;
    private MovieGenre movieGenreDuplicate;

    @BeforeEach
    void setUp() {
        product1 = Product.builder().id("prod-001").title("Movie A").build();
        product2 = Product.builder().id("prod-002").title("Movie B").build();

        movieGenre1 = MovieGenre.builder()
                .id("mg-001")
                .product(product1)
                .genre(EGenre.findByDescription("Action"))
                .build();

        movieGenre2 = MovieGenre.builder()
                .id("mg-002")
                .product(product2)
                .genre(EGenre.findByDescription("Comedy"))
                .build();

        movieGenreDuplicate = MovieGenre.builder()
                .id("mg-003-dup") // ID akan diabaikan jika duplikasi ditemukan
                .product(product1)
                .genre(EGenre.findByDescription("Action")) // Sama dengan movieGenre1
                .build();
    }

    @Test
    void create_shouldSaveNewMovieGenre_whenNoDuplicateExists() {
        when(movieGenreRepository.findAll()).thenReturn(Collections.emptyList());
        when(movieGenreRepository.saveAndFlush(any(MovieGenre.class))).thenAnswer(invocation -> {
            MovieGenre mg = invocation.getArgument(0);
            mg.setId("new-mg-id"); // Simulasikan pembuatan ID
            return mg;
        });

        MovieGenre result = movieGenreService.create(movieGenre1);

        assertNotNull(result);
        assertEquals("new-mg-id", result.getId());
        assertEquals(movieGenre1.getProduct(), result.getProduct());
        assertEquals(movieGenre1.getGenre(), result.getGenre());

        verify(movieGenreRepository, times(1)).findAll(); // Dipanggil untuk memeriksa duplikasi
        verify(movieGenreRepository, times(1)).saveAndFlush(movieGenre1); // Harus disimpan
    }

    @Test
    void create_shouldReturnExistingMovieGenre_whenDuplicateExists() {
        when(movieGenreRepository.findAll()).thenReturn(Arrays.asList(movieGenre1, movieGenre2));

        MovieGenre result = movieGenreService.create(movieGenreDuplicate);

        assertNotNull(result);
        assertEquals(movieGenre1.getId(), result.getId()); // Harus mengembalikan movieGenre1 yang sudah ada
        assertEquals(movieGenre1.getProduct(), result.getProduct());
        assertEquals(movieGenre1.getGenre(), result.getGenre());

        verify(movieGenreRepository, times(1)).findAll(); // Dipanggil untuk memeriksa duplikasi
        verify(movieGenreRepository, never()).saveAndFlush(any(MovieGenre.class)); // TIDAK boleh disimpan lagi
    }

    @Test
    void create_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(movieGenreRepository.findAll()).thenReturn(Collections.emptyList()); // Tidak ada duplikasi
        when(movieGenreRepository.saveAndFlush(any(MovieGenre.class))).thenThrow(new RuntimeException("DB error during save"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                movieGenreService.create(movieGenre1));

        assertTrue(thrown.getMessage().contains("DB error during save"));
        verify(movieGenreRepository, times(1)).findAll();
        verify(movieGenreRepository, times(1)).saveAndFlush(movieGenre1);
    }

    @Test
    void getAll_shouldReturnListOfAllMovieGenres() {
        List<MovieGenre> expectedMovieGenres = Arrays.asList(movieGenre1, movieGenre2);
        when(movieGenreRepository.findAll()).thenReturn(expectedMovieGenres);

        List<MovieGenre> result = movieGenreService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(movieGenre1));
        assertTrue(result.contains(movieGenre2));
        verify(movieGenreRepository, times(1)).findAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoMovieGenresExist() {
        when(movieGenreRepository.findAll()).thenReturn(Collections.emptyList());

        List<MovieGenre> result = movieGenreService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(movieGenreRepository, times(1)).findAll();
    }

    @Test
    void getAll_shouldThrowRuntimeException_whenRepositoryFindAllFails() {
        when(movieGenreRepository.findAll()).thenThrow(new RuntimeException("DB error during findAll"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                movieGenreService.getAll());

        assertTrue(thrown.getMessage().contains("DB error during findAll"));
        verify(movieGenreRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnMovieGenre_whenFound() {
        when(movieGenreRepository.findById(movieGenre1.getId())).thenReturn(Optional.of(movieGenre1));

        MovieGenre result = movieGenreService.getById(movieGenre1.getId());

        assertNotNull(result);
        assertEquals(movieGenre1.getId(), result.getId());
        verify(movieGenreRepository, times(1)).findById(movieGenre1.getId());
    }

    @Test
    void getById_shouldThrowRuntimeException_whenNotFound() {
        when(movieGenreRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                movieGenreService.getById("non-existent-id"));

        assertEquals(DbBash.GENRE_NOT_FOUND, thrown.getMessage());
        verify(movieGenreRepository, times(1)).findById(anyString());
    }

    @Test
    void update_shouldSaveUpdatedMovieGenre_whenFound() {
        MovieGenre updatedMovieGenre = MovieGenre.builder()
                .id(movieGenre1.getId())
                .product(product1)
                .genre(EGenre.findByDescription("Sci-Fi")) // Genre yang diperbarui
                .build();

        when(movieGenreRepository.findById(movieGenre1.getId())).thenReturn(Optional.of(movieGenre1)); // Untuk panggilan getById
        when(movieGenreRepository.saveAndFlush(any(MovieGenre.class))).thenReturn(updatedMovieGenre);

        MovieGenre result = movieGenreService.update(updatedMovieGenre);

        assertNotNull(result);
        assertEquals(updatedMovieGenre.getId(), result.getId());
        assertEquals(updatedMovieGenre.getGenre(), result.getGenre());

        verify(movieGenreRepository, times(1)).findById(movieGenre1.getId()); // Dari getById
        verify(movieGenreRepository, times(1)).saveAndFlush(updatedMovieGenre);
    }

    @Test
    void update_shouldThrowRuntimeException_whenMovieGenreToUpdateNotFound() {
        when(movieGenreRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                movieGenreService.update(movieGenre1));

        assertEquals(DbBash.GENRE_NOT_FOUND, thrown.getMessage()); // Exception dari getById
        verify(movieGenreRepository, times(1)).findById(anyString());
        verify(movieGenreRepository, never()).saveAndFlush(any(MovieGenre.class));
    }

    @Test
    void update_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(movieGenreRepository.findById(movieGenre1.getId())).thenReturn(Optional.of(movieGenre1)); // Untuk panggilan getById
        when(movieGenreRepository.saveAndFlush(any(MovieGenre.class))).thenThrow(new RuntimeException("DB error during update"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                movieGenreService.update(movieGenre1));

        assertTrue(thrown.getMessage().contains("DB error during update"));
        verify(movieGenreRepository, times(1)).findById(movieGenre1.getId());
        verify(movieGenreRepository, times(1)).saveAndFlush(movieGenre1);
    }

    @Test
    void delete_shouldDeleteMovieGenre_whenFound() {
        when(movieGenreRepository.findById(movieGenre1.getId())).thenReturn(Optional.of(movieGenre1)); // Untuk panggilan getById
        doNothing().when(movieGenreRepository).deleteById(movieGenre1.getId());

        assertDoesNotThrow(() -> movieGenreService.delete(movieGenre1.getId()));

        verify(movieGenreRepository, times(1)).findById(movieGenre1.getId()); // Dari getById
        verify(movieGenreRepository, times(1)).deleteById(movieGenre1.getId());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenMovieGenreNotFound() {
        when(movieGenreRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                movieGenreService.delete("non-existent-id"));

        assertEquals(DbBash.GENRE_NOT_FOUND, thrown.getMessage()); // Exception dari getById
        verify(movieGenreRepository, times(1)).findById(anyString());
        verify(movieGenreRepository, never()).deleteById(anyString());
    }

    @Test
    void delete_shouldThrowRuntimeException_whenRepositoryDeleteFails() {
        when(movieGenreRepository.findById(movieGenre1.getId())).thenReturn(Optional.of(movieGenre1)); // Untuk panggilan getById
        doThrow(new RuntimeException("DB error during delete")).when(movieGenreRepository).deleteById(movieGenre1.getId());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                movieGenreService.delete(movieGenre1.getId()));

        assertTrue(thrown.getMessage().contains("DB error during delete"));
        verify(movieGenreRepository, times(1)).findById(movieGenre1.getId());
        verify(movieGenreRepository, times(1)).deleteById(movieGenre1.getId());
    }
}