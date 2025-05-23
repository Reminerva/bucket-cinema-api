package com.flix.flix.service;

import org.springframework.data.domain.Page;

import com.flix.flix.entity.Studio;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.request.search.SearchStudioRequest;
import com.flix.flix.model.response.StudioResponse;

public interface StudioService {
    StudioResponse create(NewStudioRequest studioRequest);
    Page<StudioResponse> getAll(SearchStudioRequest searchStudioRequest);
    Page<StudioResponse> getAllActive(SearchStudioRequest searchStudioRequest);
    StudioResponse getById(String id);
    Studio getStudioById(String id);
    StudioResponse update(String id, NewStudioRequest studioRequest);
    void softDelete(String id);
    StudioResponse refreshAllSeat(String id);
    StudioResponse toStudioResponse(Studio studio);
}
