package com.flix.flix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.NewCustomerRequest;
import com.flix.flix.model.request.UpdateCustomerRequest;
import com.flix.flix.model.request.search.SearchCustomerRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.CustomerResponse;
import com.flix.flix.service.CustomerService;
import com.flix.flix.util.PagingUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
// @SecurityRequirement(name = "Bearer Authentication")
// @Tag(name = "Customer API", description = "API untuk mengelola customer")
@RequiredArgsConstructor
@RequestMapping(ApiBash.CUSTOMER)
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Buat customer baru", description = "Menambahkan customer baru berdasarkan user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = ApiBash.CREATE_CUSTOMER_SUCCESS)
    })
    @PostMapping()
    public ResponseEntity<CommonResponse<CustomerResponse>> createCustomer(
        @Valid
        @RequestBody
        // @Schema(example = "{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"dateOfBirth\": \"15-03-2002\", \"phone\": \"08123456789\", \"status\": \"Active\" }")
        NewCustomerRequest newCustomerRequest,
        BindingResult bindingResult
    ){
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                String message = fieldError != null
                    ? fieldError.getDefaultMessage()
                    : bindingResult.getAllErrors().get(0).getDefaultMessage();

                CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message)
                    .data(null)
                    .paging(null)
                    .build();
            
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            CustomerResponse newCustomer = customerService.create(newCustomerRequest);
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
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<List<CustomerResponse>>> getAllCustomer(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "fullname") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestParam(required = false) String fullname,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String phoneNumber,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String gender,
        @RequestParam(required = false) String registrationDate,
        @RequestParam(required = false) String lastLogin,
        @RequestParam(required = false) List<String> favGenres,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) List<String> likeProductTitle,
        @RequestParam(required = false) List<String> dislikeProductTitle,
        @RequestParam(required = false) List<String> theaterName
    ){
        try {
            SearchCustomerRequest searchCustomerRequest = SearchCustomerRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .fullname(fullname)
                .country(country)
                .phoneNumber(phoneNumber)
                .city(city)
                .gender(gender)
                .registrationDate(registrationDate)
                .lastLogin(lastLogin)
                .favGenres(favGenres)
                .email(email)
                .likeProductTitle(likeProductTitle)
                .dislikeProductTitle(dislikeProductTitle)
                .theaterName(theaterName)
                .build();
            Page<CustomerResponse> customers = customerService.getAll(searchCustomerRequest);
            CommonResponse<List<CustomerResponse>> response = CommonResponse.<List<CustomerResponse>>builder()
                                                                        .code(HttpStatus.OK.value())
                                                                        .message(ApiBash.GET_ALL_CUSTOMER_SUCCESS)
                                                                        .data(customers.getContent())
                                                                        .paging(PagingUtils.pageToPagingResponse(customers))
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
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
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
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<CustomerResponse>> updateCustomer(
        @Parameter(example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable String id,
        @Valid
        @RequestBody 
        UpdateCustomerRequest updateCustomerRequest,
        BindingResult bindingResult
    ){
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                String message = fieldError != null
                    ? fieldError.getDefaultMessage()
                    : bindingResult.getAllErrors().get(0).getDefaultMessage();

                CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                            .code(HttpStatus.BAD_REQUEST.value())
                                                                            .message(message)
                                                                            .data(null)
                                                                            .paging(null)
                                                                            .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            CustomerResponse updatedCustomer = customerService.update(id, updateCustomerRequest);
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

    @PutMapping("/me")
    public ResponseEntity<CommonResponse<CustomerResponse>> updateMe(
        HttpServletRequest httpServletRequest,
        @Valid
        @RequestBody 
        UpdateCustomerRequest updateCustomerRequest,
        BindingResult bindingResult
    ){
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                String message = fieldError != null
                    ? fieldError.getDefaultMessage()
                    : bindingResult.getAllErrors().get(0).getDefaultMessage();

                CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                                                                            .code(HttpStatus.BAD_REQUEST.value())
                                                                            .message(message)
                                                                            .data(null)
                                                                            .paging(null)
                                                                            .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            CustomerResponse updatedCustomer = customerService.updateByCredentials(httpServletRequest, updateCustomerRequest);
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
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
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
