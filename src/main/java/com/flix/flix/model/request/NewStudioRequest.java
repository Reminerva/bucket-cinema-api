package com.flix.flix.model.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class NewStudioRequest {
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "studio size is required")
    private String studioSize;
    @NotEmpty(message = "seat layout is required")
    private List<String> seatLayout;
    @NotBlank(message = "theater id is required")
    private String theaterId;

    @Builder.Default
    private List<NewStudioSeatScheduleRequest> studioSeatScheduleRequests = new ArrayList<>();
    @Builder.Default
    private List<NewProductPricingRequest> productPricingRequests = new ArrayList<>();
    @Builder.Default
    private List<NewProductSchedulingRequest> productSchedulingRequests = new ArrayList<>();
}
