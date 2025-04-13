package com.flix.flix.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.NewProductRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.ProductResponse;
import com.flix.flix.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiBash.PRODUCT)
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<CommonResponse<ProductResponse>> createProduct(
        @RequestBody @Valid NewProductRequest productRequest,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(bindingResult.getFieldError().getDefaultMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_PRODUCT_SUCCESS)
                .data(productService.create(productRequest))
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(productService.create(productRequest))
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<ProductResponse>>> getAllProduct() {
        try {
            CommonResponse<List<ProductResponse>> response = CommonResponse.<List<ProductResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_PRODUCT_SUCCESS)
                .data(productService.getAll())
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<List<ProductResponse>> response = CommonResponse.<List<ProductResponse>>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ProductResponse>> getProductById(@PathVariable String id) {
        try {
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_PRODUCT_SUCCESS)
                .data(productService.getById(id))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<ProductResponse>> updateProduct(
        @PathVariable String id,
        @RequestBody NewProductRequest productRequest,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(bindingResult.getFieldError().getDefaultMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.UPDATE_PRODUCT_SUCCESS)
                .data(productService.update(id, productRequest))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } 
    }

    @DeleteMapping("/{id}" + ApiBash.HARD_DELETE)
    public ResponseEntity<CommonResponse<ProductResponse>> hardDeleteProduct(@PathVariable String id) {
        try {
            ProductResponse productResponse = productService.getById(id);
            productService.hardDelete(id);
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.OK.value())                
                .message(ApiBash.HARD_DELETE_PRODUCT_SUCCESS)
                .data(productResponse)
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}" + ApiBash.SOFT_DELETE)
    public ResponseEntity<CommonResponse<ProductResponse>> softDeleteProduct(@PathVariable String id) {
        try {
            ProductResponse productResponse = productService.getById(id);
            productService.softDelete(id);
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.OK.value())                
                .message(ApiBash.SOFT_DELETE_PRODUCT_SUCCESS)
                .data(productResponse)
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
