package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.Product;
import com.flix.flix.model.request.NewProductRequest;
import com.flix.flix.model.response.ProductResponse;

public interface ProductService {
    ProductResponse create(NewProductRequest productRequest);
    List<ProductResponse> getAll();
    ProductResponse getById(String id);
    Product getProductById(String id);
    ProductResponse update(String id, NewProductRequest productRequest);
    void hardDelete(String id);
    void softDelete(String id);
}
