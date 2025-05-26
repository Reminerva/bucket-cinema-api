package com.flix.flix.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudioSeatScheduleResponse {
    private String id;
    private String studioId;
    private ProductSchedulingResponse productScheduling;
    private List<String> bookedSeat;
    private List<String> availableSeat;
}
