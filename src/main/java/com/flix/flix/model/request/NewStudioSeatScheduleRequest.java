package com.flix.flix.model.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewStudioSeatScheduleRequest {

    @NotBlank(message = "studio id is required")
    private String studioId;
    private List<String> bookedSeat;
    private List<String> availableSeat;
    @NotBlank(message = "product scheduling id is required")
    private String productSchedulingId;

}
