package com.flix.flix.service;

import java.util.List;

import com.flix.flix.constant.custom_enum.ESeat;
import com.flix.flix.entity.StudioSeatSchedule;
import com.flix.flix.model.request.NewStudioSeatScheduleRequest;
import com.flix.flix.model.response.StudioSeatScheduleResponse;

public interface StudioSeatScheduleService {

    StudioSeatSchedule create(NewStudioSeatScheduleRequest studioSeatScheduleRequest, List<ESeat> seatLayout);
    StudioSeatSchedule getStudioSeatScheduleById(String id);
    StudioSeatSchedule getStudioSeatScheduleByAttribute(String studioId, String productSchedulingId);
    List<StudioSeatSchedule> getAllStudioSeatSchedule();
    StudioSeatSchedule update(String id, NewStudioSeatScheduleRequest studioSeatScheduleRequest, List<ESeat> seatLayout);
    void delete(String id);
    StudioSeatScheduleResponse toStudioSeatScheduleResponse(StudioSeatSchedule studioSeatSchedule);

}
