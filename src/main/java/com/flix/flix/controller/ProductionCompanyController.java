package com.flix.flix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.NewProductionCompanyRequest;
import com.flix.flix.model.request.search.SearchProductionCompanyRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.ProductionCompanyResponse;
import com.flix.flix.service.ProductionCompanyService;
import com.flix.flix.util.PagingUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.PRODUCTION_COMPANY)
@RequiredArgsConstructor
public class ProductionCompanyController {

    private final ProductionCompanyService productionCompanyService;

    // admin only //
    @PostMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<ProductionCompanyResponse>> createProductionCompany(
        @Valid
        @RequestBody
        NewProductionCompanyRequest productionCompanyRequest
    ) {
        CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
            .code(HttpStatus.CREATED.value())
            .message(ApiBash.CREATE_PRODUCTION_COMPANY_SUCCESS)
            .data(productionCompanyService.create(productionCompanyRequest))
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<List<ProductionCompanyResponse>>> getAllProductionCompany(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "name") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String originCountry,
        @RequestParam(required = false) String foundedYearMin,
        @RequestParam(required = false) String foundedYearMax,
        @RequestParam(required = false) String headquarters,
        @RequestParam(required = false) String ceo,
        @RequestParam(required = false) String createdAtMin,
        @RequestParam(required = false) String createdAtMax,
        @RequestParam(required = false) String updatedAtMin,
        @RequestParam(required = false) String updatedAtMax,
        @RequestParam(required = false) List<String> hasProducts

    ) {
        SearchProductionCompanyRequest searchProductionCompanyRequest = SearchProductionCompanyRequest.builder()
            .page(page)
            .size(size)
            .sortBy(sortBy)
            .direction(direction)
            .name(name)
            .originCountry(originCountry)
            .foundedYearMin(foundedYearMin)
            .foundedYearMax(foundedYearMax)
            .headquarters(headquarters)
            .ceo(ceo)
            .createdAtMin(createdAtMin)
            .createdAtMax(createdAtMax)
            .updatedAtMin(updatedAtMin)
            .updatedAtMax(updatedAtMax)
            .hasProducts(hasProducts)
            .build();

        Page<ProductionCompanyResponse> productionCompanyResponses = productionCompanyService.getAll(searchProductionCompanyRequest);
        CommonResponse<List<ProductionCompanyResponse>> response = CommonResponse.<List<ProductionCompanyResponse>>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_ALL_PRODUCTION_COMPANY_SUCCESS)
            .data(productionCompanyResponses.getContent())
            .paging(PagingUtils.pageToPagingResponse(productionCompanyResponses))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<ProductionCompanyResponse>> updateProductionCompany(
        @PathVariable String id, 
        @Valid
        @RequestBody 
        NewProductionCompanyRequest productionCompanyRequest
    ) {
        CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.UPDATE_PRODUCTION_COMPANY_SUCCESS)
            .data(productionCompanyService.update(id, productionCompanyRequest))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<ProductionCompanyResponse>> deleteProductionCompany(
        @PathVariable String id
    ) {
        productionCompanyService.delete(id);
        CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.DELETE_PRODUCTION_COMPANY_SUCCESS)
            .data(null)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // admin, cashier, customer only //
    @GetMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN + " || " + ApiBash.HAS_ROLE_CASHIER + " || " + ApiBash.HAS_ROLE_CUSTOMER)
    public ResponseEntity<CommonResponse<ProductionCompanyResponse>> getProductionCompanyById(
        @PathVariable String id
    ) {
        CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_PRODUCTION_COMPANY_SUCCESS)
            .data(productionCompanyService.getById(id))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
