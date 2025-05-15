package com.flix.flix.service;

import org.springframework.data.domain.Page;

import com.flix.flix.entity.Product;
import com.flix.flix.model.request.NewProductRequest;
import com.flix.flix.model.request.search.SearchProductRequest;
import com.flix.flix.model.response.ProductResponse;

public interface ProductService {
    ProductResponse create(NewProductRequest productRequest);
    Page<ProductResponse> getAll(SearchProductRequest searchProductRequest);
    ProductResponse getById(String id);
    Product getProductById(String id);
    ProductResponse update(String id, NewProductRequest productRequest);
    void hardDelete(String id);
    void softDelete(String id);
}
