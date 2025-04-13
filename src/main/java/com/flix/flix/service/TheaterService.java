package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.Theater;
import com.flix.flix.model.request.NewTheaterRequest;
import com.flix.flix.model.response.TheaterResponse;

public interface TheaterService {
    TheaterResponse create(NewTheaterRequest theaterRequest);
    TheaterResponse getById(String id);
    Theater getTheaterById(String id);
    List<TheaterResponse> getAll();
    TheaterResponse update(String id, NewTheaterRequest theaterRequest);
    void softDelete(String id);
}
