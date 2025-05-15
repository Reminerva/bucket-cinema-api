package com.flix.flix.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.flix.flix.entity.ProductionCompany;
import com.flix.flix.model.request.NewProductionCompanyRequest;
import com.flix.flix.model.request.search.SearchProductionCompanyRequest;
import com.flix.flix.model.response.ProductionCompanyResponse;

public interface ProductionCompanyService {
    ProductionCompanyResponse create(NewProductionCompanyRequest productionCompanyRequest);
    Page<ProductionCompanyResponse> getAll(SearchProductionCompanyRequest searchProductionCompanyRequest);
    ProductionCompanyResponse getById(String id);
    ProductionCompany getProductionCompanyById(String id);
    ProductionCompanyResponse update(String id, NewProductionCompanyRequest productionCompanyRequest);
    void delete(String id);
}
