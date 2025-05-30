package com.flix.flix.entity;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ArtistTest {

    @Test
    void testEqualsTo() {

        Artist artist1 = Artist.builder().id("id1").build();
        Artist artist2 = Artist.builder().id("id1").build();

        assertTrue(artist1.equalsTo(artist2));
    }
}
