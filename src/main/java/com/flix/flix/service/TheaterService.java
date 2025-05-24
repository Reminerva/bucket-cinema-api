package com.flix.flix.service;

import org.springframework.data.domain.Page;

import com.flix.flix.entity.Theater;
import com.flix.flix.model.request.NewTheaterRequest;
import com.flix.flix.model.request.search.SearchTheaterRequest;
import com.flix.flix.model.response.TheaterResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface TheaterService {
    TheaterResponse create(NewTheaterRequest theaterRequest);
    TheaterResponse getById(String id);
    Theater getTheaterById(String id);
    Page<TheaterResponse> getAll(SearchTheaterRequest searchTheaterRequest, HttpServletRequest httpServletRequest);
    TheaterResponse update(String id, NewTheaterRequest theaterRequest);
    void softDelete(String id);
    TheaterResponse refreshAllSeat(String id);
}
