package com.flix.flix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.NewCustomerRequest;
import com.flix.flix.model.request.UpdateCustomerRequest;
import com.flix.flix.model.request.search.SearchCustomerRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.CustomerResponse;
import com.flix.flix.service.CustomerService;
import com.flix.flix.util.PagingUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiBash.CUSTOMER)
public class CustomerController {

    private final CustomerService customerService;

    // admin only //
    @PostMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<CustomerResponse>> createCustomer(
        @Valid
        @RequestBody
        NewCustomerRequest newCustomerRequest
    ){
        CustomerResponse newCustomer = customerService.create(newCustomerRequest);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
            .code(HttpStatus.CREATED.value())
            .message(ApiBash.CREATE_CUSTOMER_SUCCESS)
            .data(newCustomer)
            .paging(null)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

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
        @RequestParam(required = false) String registrationDateMin,
        @RequestParam(required = false) String registrationDateMax,
        @RequestParam(required = false) String birthDateMin,
        @RequestParam(required = false) String birthDateMax,
        @RequestParam(required = false) String lastLoginMin,
        @RequestParam(required = false) String lastLoginMax,
        @RequestParam(required = false) List<String> favGenres,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) List<String> likeProductTitle,
        @RequestParam(required = false) List<String> dislikeProductTitle
    ){
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
            .birthDateMin(birthDateMin)
            .birthDateMax(birthDateMax)
            .registrationDateMin(registrationDateMin)
            .registrationDateMax(registrationDateMax)
            .lastLoginMin(lastLoginMin)
            .lastLoginMax(lastLoginMax)
            .favGenres(favGenres)
            .email(email)
            .likeProductTitle(likeProductTitle)
            .dislikeProductTitle(dislikeProductTitle)
            .build();
        Page<CustomerResponse> customers = customerService.getAll(searchCustomerRequest);
        CommonResponse<List<CustomerResponse>> response = CommonResponse.<List<CustomerResponse>>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_ALL_CUSTOMER_SUCCESS)
            .data(customers.getContent())
            .paging(PagingUtils.pageToPagingResponse(customers))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<CustomerResponse>> getCustomerById(
        @PathVariable String id
    ){
        CustomerResponse customer = customerService.getById(id);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_CUSTOMER_SUCCESS)
            .data(customer)
            .paging(null)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<CustomerResponse>> updateCustomer(
        @PathVariable String id,
        @Valid
        @RequestBody
        UpdateCustomerRequest updateCustomerRequest
    ){
        CustomerResponse updatedCustomer = customerService.update(id, updateCustomerRequest);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.UPDATE_CUSTOMER_SUCCESS)
            .data(updatedCustomer)
            .paging(null)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<CustomerResponse>> deleteCustomer(
        @PathVariable String id
    ){
        CustomerResponse customer = customerService.getById(id);
        customerService.delete(id);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.DELETE_CUSTOMER_SUCCESS)
            .data(customer)
            .paging(null)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // customer only //
    @GetMapping("/me")
    @PreAuthorize(ApiBash.HAS_ROLE_CUSTOMER)
    public ResponseEntity<CommonResponse<CustomerResponse>> getMe(
        HttpServletRequest httpServletRequest
    ) {
        CustomerResponse customer = customerService.getByCredentials(httpServletRequest);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_CUSTOMER_SUCCESS)
            .data(customer)
            .paging(null)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/me")
    @PreAuthorize(ApiBash.HAS_ROLE_CUSTOMER)
    public ResponseEntity<CommonResponse<CustomerResponse>> updateMe(
        HttpServletRequest httpServletRequest,
        @Valid
        @RequestBody 
        UpdateCustomerRequest updateCustomerRequest
    ){
        CustomerResponse updatedCustomer = customerService.updateByCredentials(httpServletRequest, updateCustomerRequest);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.UPDATE_CUSTOMER_SUCCESS)
            .data(updatedCustomer)
            .paging(null)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
