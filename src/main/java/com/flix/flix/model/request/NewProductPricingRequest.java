package com.flix.flix.model.request;

import jakarta.validation.constraints.Min;
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
public class NewProductPricingRequest {
    @Min(value = 0, message = "weekday price is required")
    private Double weekdayPrice;
    @Min(value = 0, message = "weekend price is required")
    private Double weekendPrice;
    @NotBlank(message = "weekday price date is required")
    private Boolean weekdayPriceActive;
    private Boolean weekendPriceActive;
    @NotBlank(message = "product id is required")
    private String productIdPricing;
}
