package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.Artist;
import com.flix.flix.model.request.NewArtistRequest;
import com.flix.flix.model.response.ArtistResponse;

public interface ArtistService {
    ArtistResponse create(NewArtistRequest artistRequest);
    List<ArtistResponse> getAll();
    ArtistResponse getById(String id);
    Artist findById(String id);
    ArtistResponse update(String id, NewArtistRequest artistRequest);
    void delete(String id);
}
