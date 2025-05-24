package com.flix.flix.service;

import org.springframework.data.domain.Page;

import com.flix.flix.entity.Artist;
import com.flix.flix.model.request.NewArtistRequest;
import com.flix.flix.model.request.search.SearchArtistRequest;
import com.flix.flix.model.response.ArtistResponse;

public interface ArtistService {
    ArtistResponse create(NewArtistRequest artistRequest);
    Page<ArtistResponse> getAll(SearchArtistRequest searchArtistRequest);
    ArtistResponse getById(String id);
    Artist getArtistById(String id);
    ArtistResponse update(String id, NewArtistRequest artistRequest);
    void delete(String id);
}
