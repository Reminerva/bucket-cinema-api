package com.flix.flix.model.request;

import java.util.List;

import jakarta.validation.constraints.Min;
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
public class NewProductPricingSchedulingRequest {
    @NotBlank(message = "product id is required")
    private String productId;
    @Min(value = 0, message = "weekday price is required")
    private Double weekdayPrice;
    @Min(value = 0, message = "weekend price is required")
    private Double weekendPrice;

    @NotEmpty(message = "schedule is required")
    private List<String> schedule;
}
