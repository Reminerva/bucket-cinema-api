package com.flix.flix.entity;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.flix.flix.constant.custom_enum.EGenre;

public class CustomerTest {

    @Test
    void testEqualsTo() {

        Customer customer1 = Customer.builder().id("id1").build();
        Customer customer2 = Customer.builder().id("id1").build();

        assertTrue(customer1.equalsTo(customer2));
    }

    @Test
    void testContainingFavGenre() {
        // 1. Persiapan: Buat Customer dengan daftar FavGenre
        // Menggunakan EGenre.GENRE_ACTION dan EGenre.GENRE_COMEDY
        FavGenre genreAction = FavGenre.builder().id("g1").favGenre(EGenre.GENRE_ACTION).build();
        FavGenre genreComedy = FavGenre.builder().id("g2").favGenre(EGenre.GENRE_COMEDY).build();
        List<FavGenre> favGenres = new ArrayList<>(Arrays.asList(genreAction, genreComedy));

        Customer customer = Customer.builder()
                .id("c1")
                .fullname("Test Customer")
                .favGenre(favGenres) // Set daftar genre favorit
                .build();

        // 2. Eksekusi: Panggil metode dengan genre yang ada
        Boolean containsAction = customer.containingFavGenre("Genre_Action"); // Akan cocok dengan EGenre.GENRE_ACTION.getDisplayName()
        Boolean containsComedy = customer.containingFavGenre("Genre_comedy"); // Uji case-insensitivity

        // 3. Verifikasi
        assertTrue(containsAction, "Should return true for 'Action' genre");
        assertTrue(containsComedy, "Should return true for 'comedy' genre (case-insensitive)");
    }
}
