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
import com.flix.flix.model.request.NewProductRequest;
import com.flix.flix.model.request.search.SearchProductRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.ProductResponse;
import com.flix.flix.service.ProductService;
import com.flix.flix.util.PagingUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiBash.PRODUCT)
public class ProductController {

    private final ProductService productService;

    // admin only //
    @PostMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<ProductResponse>> createProduct(
        @RequestBody @Valid NewProductRequest productRequest
    ) {
        CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
            .code(HttpStatus.CREATED.value())
            .message(ApiBash.CREATE_PRODUCT_SUCCESS)
            .data(productService.create(productRequest))
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<List<ProductResponse>>> getAllProduct(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "title") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) Long durationMin,
        @RequestParam(required = false) Long durationMax,
        @RequestParam(required = false) String language,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String releaseDateMin,
        @RequestParam(required = false) String releaseDateMax,
        @RequestParam(required = false) String rated,
        @RequestParam(required = false) Long budgetMin,
        @RequestParam(required = false) Long budgetMax,
        @RequestParam(required = false) Double imdbRatingMin,
        @RequestParam(required = false) Double imdbRatingMax,
        @RequestParam(required = false) Integer rottenTomatoesRatingMin,
        @RequestParam(required = false) Integer rottenTomatoesRatingMax,
        @RequestParam(required = false) List<String> movieGenre,
        @RequestParam(required = false) Double productPricingMin,
        @RequestParam(required = false) Double productPricingMax,
        @RequestParam(required = false) String lastUpdatedMin,
        @RequestParam(required = false) String lastUpdatedMax,
        @RequestParam(required = false) List<String> artistsName,
        @RequestParam(required = false) String productionCompany
    ) {
        SearchProductRequest searchProductRequest = SearchProductRequest.builder()
            .page(page)
            .size(size)
            .sortBy(sortBy)
            .direction(direction)
            .title(title)
            .durationMin(durationMin)
            .durationMax(durationMax)
            .language(language)
            .country(country)
            .releaseDateMin(releaseDateMin)
            .releaseDateMax(releaseDateMax)
            .rated(rated)
            .budgetMin(budgetMin)
            .budgetMax(budgetMax)
            .imdbRatingMin(imdbRatingMin)
            .imdbRatingMax(imdbRatingMax)
            .rottenTomatoesRatingMin(rottenTomatoesRatingMin)
            .rottenTomatoesRatingMax(rottenTomatoesRatingMax)
            .movieGenre(movieGenre)
            .productPricingMin(productPricingMin)
            .productPricingMax(productPricingMax)
            .lastUpdatedMin(lastUpdatedMin)
            .lastUpdatedMax(lastUpdatedMax)
            .artistsName(artistsName)
            .productionCompany(productionCompany)
            .build();
        Page<ProductResponse> products = productService.getAll(searchProductRequest);
        CommonResponse<List<ProductResponse>> response = CommonResponse.<List<ProductResponse>>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_ALL_PRODUCT_SUCCESS)
            .data(products.getContent())
            .paging(PagingUtils.pageToPagingResponse(products))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<ProductResponse>> updateProduct(
        @PathVariable String id,
        @RequestBody NewProductRequest productRequest
    ) {
        CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.UPDATE_PRODUCT_SUCCESS)
            .data(productService.update(id, productRequest))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}" + ApiBash.HARD_DELETE)
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<ProductResponse>> hardDeleteProduct(
        @PathVariable String id
    ) {
        ProductResponse productResponse = productService.getById(id);
        productService.hardDelete(id);
        CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.HARD_DELETE_PRODUCT_SUCCESS)
            .data(productResponse)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}" + ApiBash.SOFT_DELETE)
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<ProductResponse>> softDeleteProduct(
        @PathVariable String id
    ) {
        ProductResponse productResponse = productService.getById(id);
        productService.softDelete(id);
        CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.SOFT_DELETE_PRODUCT_SUCCESS)
            .data(productResponse)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // admin, cashier, customer only //
    @GetMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN + " || " + ApiBash.HAS_ROLE_CASHIER + " || " + ApiBash.HAS_ROLE_CUSTOMER)
    public ResponseEntity<CommonResponse<ProductResponse>> getProductById(
        @PathVariable String id
    ) {
        CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_PRODUCT_SUCCESS)
            .data(productService.getById(id))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
