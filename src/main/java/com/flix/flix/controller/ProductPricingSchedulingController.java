package com.flix.flix.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.NewProductPricingSchedulingRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.ProductPricingSchedulingResponse;
import com.flix.flix.service.ProductPricingSchedulingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.PRODUCT_PRICING_SCHEDULING)
@RequiredArgsConstructor
public class ProductPricingSchedulingController {

    private final ProductPricingSchedulingService productPricingSchedulingService;

    @PostMapping
    public ResponseEntity<CommonResponse<ProductPricingSchedulingResponse>> createProductPricingScheduling(
        @Valid
        @RequestBody
        NewProductPricingSchedulingRequest productPricingSchedulingRequest,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            CommonResponse<ProductPricingSchedulingResponse> response = CommonResponse.<ProductPricingSchedulingResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(bindingResult.getFieldError().getDefaultMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            CommonResponse<ProductPricingSchedulingResponse> response = CommonResponse.<ProductPricingSchedulingResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_PRODUCT_PRICING_SCHEDULING_SUCCESS)
                .data(productPricingSchedulingService.create(productPricingSchedulingRequest))
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            CommonResponse<ProductPricingSchedulingResponse> response = CommonResponse.<ProductPricingSchedulingResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<ProductPricingSchedulingResponse>>> getAllProductPricingScheduling() {
        try {
            CommonResponse<List<ProductPricingSchedulingResponse>> response = CommonResponse.<List<ProductPricingSchedulingResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_PRODUCT_PRICING_SCHEDULING_SUCCESS)
                .data(productPricingSchedulingService.getAll())
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<List<ProductPricingSchedulingResponse>> response = CommonResponse.<List<ProductPricingSchedulingResponse>>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<ProductPricingSchedulingResponse>> deleteProductPricingScheduling(@PathVariable String id) {
        try {
            ProductPricingSchedulingResponse productPricingSchedulingResponse = productPricingSchedulingService.getById(id);
            productPricingSchedulingService.delete(id);
            CommonResponse<ProductPricingSchedulingResponse> response = CommonResponse.<ProductPricingSchedulingResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.DELETE_PRODUCT_PRICING_SCHEDULING_SUCCESS)
                .data(productPricingSchedulingResponse)
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<ProductPricingSchedulingResponse> response = CommonResponse.<ProductPricingSchedulingResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
