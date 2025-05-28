package com.flix.flix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flix.flix.entity.ProductArtist;

@Repository
public interface ProductArtistRepository extends JpaRepository<ProductArtist, String> {
    Optional<ProductArtist> findByProductIdAndArtistId(String productId, String artistId);
}
