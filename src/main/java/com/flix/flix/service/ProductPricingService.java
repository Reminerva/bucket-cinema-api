package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.ProductPricing;
import com.flix.flix.model.request.NewProductPricingRequest;
import com.flix.flix.model.response.ProductPricingResponse;

public interface ProductPricingService {
    ProductPricing create(NewProductPricingRequest productPricingRequest);
    List<ProductPricing> getAll();
    ProductPricing getProductPricingById(String id);
    List<ProductPricing> getProductPricingByProductId(String id);
    ProductPricing getProductPricingByPrice(List<ProductPricing> productPricings, Double weekdayPrice, Double weekendPrice);
    ProductPricing update(String id, NewProductPricingRequest productPricingRequest);
    void softDelete(String id);
    ProductPricingResponse toProductPricingResponse(ProductPricing productPricing);
}
