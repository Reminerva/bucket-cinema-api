package com.flix.flix.model.response;

import java.util.List;

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
public class ProductPricingSchedulingResponse {
    private String id;
    private String productId;
    private ProductPricingResponse productPricing;
    private List<ProductSchedulingResponse> productScheduling;
}
