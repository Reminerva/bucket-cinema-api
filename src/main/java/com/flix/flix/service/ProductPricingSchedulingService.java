package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.ProductPricingScheduling;
import com.flix.flix.model.request.NewProductPricingSchedulingRequest;
import com.flix.flix.model.response.ProductPricingSchedulingResponse;

public interface ProductPricingSchedulingService {
    ProductPricingSchedulingResponse create(NewProductPricingSchedulingRequest productPricingSchedulingRequest);
    List<ProductPricingSchedulingResponse> getAll();
    ProductPricingSchedulingResponse getById(String id);
    ProductPricingScheduling getProductPricingSchedulingById(String id);
    ProductPricingScheduling getProductPricingSchedulingByAttribute(NewProductPricingSchedulingRequest productPricingSchedulingRequest);
    ProductPricingSchedulingResponse update(String id, NewProductPricingSchedulingRequest productPricingSchedulingRequest);
    void delete(String id);
    ProductPricingSchedulingResponse toProductPricingSchedulingResponse(ProductPricingScheduling productPricingScheduling);
}
