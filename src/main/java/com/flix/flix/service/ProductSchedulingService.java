package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.model.request.NewProductSchedulingRequest;
import com.flix.flix.model.response.ProductSchedulingResponse;

public interface ProductSchedulingService {
    ProductScheduling create(NewProductSchedulingRequest productSchedulingRequest);
    List<ProductScheduling> getAll();
    ProductScheduling getProductSchedulingById(String id);
    ProductScheduling getProductSchedulingByAttribute(NewProductSchedulingRequest productSchedulingRequest);
    ProductScheduling update(String id, NewProductSchedulingRequest productSchedulingRequest);
    void delete(String id);
    ProductSchedulingResponse toProductSchedulingResponse(ProductScheduling productScheduling);
}
