package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.Studio;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.response.StudioResponse;

public interface StudioService {
    StudioResponse create(NewStudioRequest studioRequest);
    List<StudioResponse> getAll();
    StudioResponse getById(String id);
    Studio getStudioById(String id);
    StudioResponse update(String id, NewStudioRequest studioRequest);
    void delete(String id);
    StudioResponse toStudioResponse(Studio studio);
}
