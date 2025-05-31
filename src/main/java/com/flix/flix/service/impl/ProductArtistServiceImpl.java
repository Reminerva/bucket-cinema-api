package com.flix.flix.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EArtistType;
import com.flix.flix.entity.Artist;
import com.flix.flix.entity.ProductArtist;
import com.flix.flix.model.response.ProductArtistResponse;
import com.flix.flix.repository.ArtistRepository;
import com.flix.flix.repository.ProductArtistRepository;
import com.flix.flix.service.ProductArtistService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductArtistServiceImpl implements ProductArtistService {

    private final ProductArtistRepository productArtistRepository;
    private final ArtistRepository artistRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductArtistResponse create(ProductArtist productArtist) {
        try {
            Artist artist = artistRepository.findById(productArtist.getArtist().getId()).orElseThrow(() -> new RuntimeException(DbBash.ARTIST_NOT_FOUND));

            if (productArtist.getArtistType() != null) {
                for (EArtistType artistType : productArtist.getArtistType()) {
                    if (!artist.getArtistTypes().contains(artistType)) {
                        artist.getArtistTypes().add(artistType);
                    }
                }
            }

            return toProductArtistResponse(productArtistRepository.saveAndFlush(productArtist));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductArtist getProductArtistById(String id) {
        try {
            Optional<ProductArtist> productArtist = productArtistRepository.findById(id);
            if (productArtist.isEmpty()) throw new RuntimeException(DbBash.PRODUCT_ARTIST_NOT_FOUND);
            return productArtist.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductArtistResponse getById(String id) {
        try {
            return toProductArtistResponse(getProductArtistById(id));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductArtist getProductArtistByProductIdAndArtistId(String productId, String artistId) {
        try {
            Optional<ProductArtist> productArtist = productArtistRepository.findByProductIdAndArtistId(productId, artistId);
            if (productArtist.isEmpty()) {return null;}
            return productArtist.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<ProductArtistResponse> getAll() {
        try {
            return productArtistRepository.findAll().stream().map(this::toProductArtistResponse).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductArtistResponse update(String id, ProductArtist productArtist) {
        try {
            ProductArtist productArtistUpdate = getProductArtistById(id);
            productArtistUpdate.setProduct(productArtist.getProduct());
            productArtistUpdate.setArtist(productArtist.getArtist());
            productArtistUpdate.setArtistType(productArtist.getArtistType());

            Artist artist = artistRepository.findById(productArtist.getArtist().getId()).orElseThrow(() -> new RuntimeException(DbBash.ARTIST_NOT_FOUND));
            for (EArtistType artistType : productArtist.getArtistType()) {
                if (!artist.getArtistTypes().contains(artistType)) {
                    artist.getArtistTypes().add(artistType);
                }
            }

            return toProductArtistResponse(productArtistRepository.saveAndFlush(productArtistUpdate));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(String id) {
        try {
            getProductArtistById(id);
            productArtistRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductArtistResponse toProductArtistResponse(ProductArtist productArtist) {
        try {
            return ProductArtistResponse.builder()
                .id(productArtist.getId())
                .productId(productArtist.getProduct() == null ? null : productArtist.getProduct().getId())
                .productTitle(productArtist.getProduct() == null ? null : productArtist.getProduct().getTitle())
                .artistId(productArtist.getArtist() == null ? null : productArtist.getArtist().getId())
                .artistName(productArtist.getArtist() == null ? null : productArtist.getArtist().getName())
                .artistType(productArtist.getArtistType() == null || productArtist.getArtistType().isEmpty() ? null : EArtistType.toEArtistTypeStringList(productArtist.getArtistType()))
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
