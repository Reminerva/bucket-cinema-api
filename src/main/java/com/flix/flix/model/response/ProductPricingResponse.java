package com.flix.flix.model.response;

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
public class ProductPricingResponse {
    private String id;
    private Double weekdayPrice;
    private Double weekendPrice;
    private String priceDate;
    private Boolean isPriceActive;
    private String productId;
}
