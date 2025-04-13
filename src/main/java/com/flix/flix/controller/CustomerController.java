package com.flix.flix.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.entity.AppUser;
import com.flix.flix.entity.Customer;
import com.flix.flix.model.request.NewCustomerRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.CustomerResponse;
import com.flix.flix.service.AppUserService;
import com.flix.flix.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
// @SecurityRequirement(name = "Bearer Authentication")
// @Tag(name = "Customer API", description = "API untuk mengelola customer")
@RequiredArgsConstructor
@RequestMapping(ApiBash.CUSTOMER)
public class CustomerController {

    private final CustomerService customerService;
    private final AppUserService appUserService;

    @Operation(summary = "Buat customer baru", description = "Menambahkan customer baru berdasarkan user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = ApiBash.CREATE_CUSTOMER_SUCCESS)
    })
    @PostMapping("/{id}")
    public ResponseEntity<CommonResponse<CustomerResponse>> createCustomer(
        @Parameter(example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable String id,
        @Valid
        @RequestBody
        // @Schema(example = "{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"dateOfBirth\": \"15-03-2002\", \"phone\": \"08123456789\", \"status\": \"Active\" }")
        NewCustomerRequest newCustomerRequest,
        BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                        .code(HttpStatus.BAD_REQUEST.value())
                                                                        .message(bindingResult.getFieldError().getDefaultMessage())
                                                                        .data(null)
                                                                        .paging(null)
                                                                        .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            AppUser appUser = appUserService.getAppUserById(id);
            CustomerResponse newCustomer = customerService.create(appUser, newCustomerRequest);
            CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                            .code(HttpStatus.CREATED.value())
                                                                            .message(ApiBash.CREATE_CUSTOMER_SUCCESS)
                                                                            .data(newCustomer)
                                                                            .paging(null)
                                                                            .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                            .code(HttpStatus.BAD_REQUEST.value())
                                                                            .message(e.getMessage())
                                                                            .data(null)
                                                                            .paging(null)
                                                                            .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
    }

    @Operation(summary = "Get all customers", description = "Mengambil semua customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = ApiBash.GET_ALL_CUSTOMER_SUCCESS)
    })
    @GetMapping
    public ResponseEntity<CommonResponse<List<CustomerResponse>>> getAllCustomer(){
        try {
            CommonResponse<List<CustomerResponse>> response = CommonResponse.<List<CustomerResponse>>builder()
                                                                        .code(HttpStatus.OK.value())
                                                                        .message(ApiBash.GET_ALL_CUSTOMER_SUCCESS)
                                                                        .data(customerService.getAll())
                                                                        .paging(null)
                                                                        .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<List<CustomerResponse>> response = CommonResponse.<List<CustomerResponse>>builder()
                                                                        .code(HttpStatus.BAD_REQUEST.value())
                                                                        .message(e.getMessage())
                                                                        .data(null)
                                                                        .paging(null)
                                                                        .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "Get customer by ID", description = "Mengambil customer berdasarkan ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = ApiBash.GET_CUSTOMER_SUCCESS)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<CustomerResponse>> getCustomerById(
        @Parameter(example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable String id
    ){
        try {
            CustomerResponse customer = customerService.getById(id);
            CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                        .code(HttpStatus.OK.value())
                                                                        .message(ApiBash.GET_CUSTOMER_SUCCESS)
                                                                        .data(customer)
                                                                        .paging(null)
                                                                        .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                        .code(HttpStatus.BAD_REQUEST.value())
                                                                        .message(e.getMessage())
                                                                        .data(null)
                                                                        .paging(null)
                                                                        .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "Update customer", description = "Mengupdate customer berdasarkan ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = ApiBash.UPDATE_CUSTOMER_SUCCESS)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<CustomerResponse>> updateCustomer(
        @Parameter(example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable String id,
        @Valid
        @RequestBody 
        NewCustomerRequest newCustomerRequest,
        BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                        .code(HttpStatus.BAD_REQUEST.value())
                                                                        .message(bindingResult.getFieldError().getDefaultMessage())
                                                                        .data(null)
                                                                        .paging(null)
                                                                        .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            CustomerResponse updatedCustomer = customerService.update(id, newCustomerRequest);
            CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                        .code(HttpStatus.OK.value())
                                                                        .message(ApiBash.UPDATE_CUSTOMER_SUCCESS)
                                                                        .data(updatedCustomer)
                                                                        .paging(null)
                                                                        .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                        .code(HttpStatus.BAD_REQUEST.value())
                                                                        .message(e.getMessage())
                                                                        .data(null)
                                                                        .paging(null)
                                                                        .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
    }

    @Operation(summary = "Delete customer", description = "Menghapus customer berdasarkan ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = ApiBash.DELETE_CUSTOMER_SUCCESS)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<CustomerResponse>> deleteCustomer(
        @Parameter(example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable String id
    ){
        try {
            CustomerResponse customer = customerService.getById(id);
            customerService.delete(id);
            CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                        .code(HttpStatus.OK.value())
                                                                        .message(ApiBash.DELETE_CUSTOMER_SUCCESS)
                                                                        .data(customer)
                                                                        .paging(null)
                                                                        .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                        .code(HttpStatus.BAD_REQUEST.value())
                                                                        .message(e.getMessage())
                                                                        .data(null)
                                                                        .paging(null)
                                                                        .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
