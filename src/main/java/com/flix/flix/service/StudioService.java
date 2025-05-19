package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.Studio;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.response.StudioResponse;

public interface StudioService {
    StudioResponse create(NewStudioRequest studioRequest);
    List<StudioResponse> getAll();
    List<StudioResponse> getAllActive();
    StudioResponse getById(String id);
    Studio getStudioById(String id);
    StudioResponse update(String id, NewStudioRequest studioRequest);
    void softDelete(String id);
    StudioResponse toStudioResponse(Studio studio);
}
