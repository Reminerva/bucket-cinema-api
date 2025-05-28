package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.ProductArtist;
import com.flix.flix.model.response.ProductArtistResponse;

public interface ProductArtistService {

    ProductArtistResponse create(ProductArtist productArtist);
    ProductArtist getProductArtistById(String id);
    ProductArtistResponse getById(String id);
    ProductArtist getProductArtistByProductIdAndArtistId(String productId, String artistId);
    List<ProductArtistResponse> getAll();
    ProductArtistResponse update(String id, ProductArtist productArtist);
    void delete(String id);
    ProductArtistResponse toProductArtistResponse(ProductArtist productArtist);

}
