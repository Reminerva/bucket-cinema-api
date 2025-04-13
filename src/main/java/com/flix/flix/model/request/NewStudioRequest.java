package com.flix.flix.model.request;

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
    private List<String> bookedSeat;
    @NotEmpty(message = "available seat is required")
    private List<String> availableSeat;

    private List<NewProductPricingSchedulingRequest> productPricingScheduling;
}
