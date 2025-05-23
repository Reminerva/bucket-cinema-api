package com.flix.flix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

    @PostMapping
    public ResponseEntity<CommonResponse<ProductionCompanyResponse>> create(
        @Valid
        @RequestBody
        NewProductionCompanyRequest productionCompanyRequest,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
                String message = fieldError != null
                    ? fieldError.getDefaultMessage()
                    : bindingResult.getAllErrors().get(0).getDefaultMessage();

            CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_PRODUCTION_COMPANY_SUCCESS)
                .data(productionCompanyService.create(productionCompanyRequest))
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<ProductionCompanyResponse>>> getAll(
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
        try {
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
        } catch (Exception e) {
            CommonResponse<List<ProductionCompanyResponse>> response = CommonResponse.<List<ProductionCompanyResponse>>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ProductionCompanyResponse>> getById(@PathVariable String id) {
        try {
            CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_PRODUCTION_COMPANY_SUCCESS)
                .data(productionCompanyService.getById(id))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<ProductionCompanyResponse>> update(
        @PathVariable String id, 
        @Valid
        @RequestBody 
        NewProductionCompanyRequest productionCompanyRequest,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
                String message = fieldError != null
                    ? fieldError.getDefaultMessage()
                    : bindingResult.getAllErrors().get(0).getDefaultMessage();

            CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.UPDATE_PRODUCTION_COMPANY_SUCCESS)
                .data(productionCompanyService.update(id, productionCompanyRequest))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<ProductionCompanyResponse>> delete(@PathVariable String id) {
        try {
            ProductionCompanyResponse productionCompanyResponse = productionCompanyService.getById(id);
            productionCompanyService.delete(id);
            CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.DELETE_PRODUCTION_COMPANY_SUCCESS)
                .data(productionCompanyResponse)
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<ProductionCompanyResponse> response = CommonResponse.<ProductionCompanyResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
