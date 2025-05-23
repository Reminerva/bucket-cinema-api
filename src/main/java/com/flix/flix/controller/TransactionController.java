package com.flix.flix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.NewTransactionRequest;
import com.flix.flix.model.request.search.SearchTransactionRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.TransactionResponse;
import com.flix.flix.service.TransactionService;
import com.flix.flix.util.PagingUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.TRANSACTION)
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<CommonResponse<TransactionResponse>> createTransaction(
        @Valid
        @RequestBody
        NewTransactionRequest transactionRequest,
        HttpServletRequest httpServletRequest,
        BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                    String message = fieldError != null
                        ? fieldError.getDefaultMessage()
                        : bindingResult.getAllErrors().get(0).getDefaultMessage();
    
                CommonResponse<TransactionResponse> response = CommonResponse.<TransactionResponse>builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(message)
                        .data(null)
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            CommonResponse<TransactionResponse> response = CommonResponse.<TransactionResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_TRANSACTION_SUCCESS)
                .data(transactionService.create(transactionRequest, httpServletRequest))
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            CommonResponse<TransactionResponse> response = CommonResponse.<TransactionResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<TransactionResponse>> getById(@PathVariable String id) {
        try {
            CommonResponse<TransactionResponse> response = CommonResponse.<TransactionResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_TRANSACTION_SUCCESS)
                .data(transactionService.getById(id))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<TransactionResponse> response = CommonResponse.<TransactionResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<TransactionResponse>>> getAllTransaction(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "customer.fullname") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestParam(required = false) String customerName,
        @RequestParam(required = false) String employeeName,
        @RequestParam(required = false) String theaterName,
        @RequestParam(required = false) String studioName,
        @RequestParam(required = false) String productTitle,
        @RequestParam(required = false) Double productPriceMin,
        @RequestParam(required = false) Double productPriceMax,
        @RequestParam(required = false) String productSchedule,
        @RequestParam(required = false) Integer qtyMin,
        @RequestParam(required = false) Integer qtyMax,
        @RequestParam(required = false) Integer tax,
        @RequestParam(required = false) String transactionDateTimeMin,
        @RequestParam(required = false) String transactionDateTimeMax,
        @RequestParam(required = false) String watchDateMin,
        @RequestParam(required = false) String watchDateMax,
        @RequestParam(required = false) String paymentStatus,
        @RequestParam(required = false) String paymentDateTimeMin,
        @RequestParam(required = false) String paymentDateTimeMax,
        @RequestParam(required = false) String paymentMethod,
        @RequestParam(required = false) List<String> seats,
        @RequestParam(required = false) String createdAtMin,
        @RequestParam(required = false) String createdAtMax,
        @RequestParam(required = false) String updatedAtMin,
        @RequestParam(required = false) String updatedAtMax,
        @RequestParam(required = false) String expirationDateMin,
        @RequestParam(required = false) String expirationDateMax

    ) {
        try {
            SearchTransactionRequest request = SearchTransactionRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .customerName(customerName)
                .employeeName(employeeName)
                .theaterName(theaterName)
                .studioName(studioName)
                .productTitle(productTitle)
                .productPriceMin(productPriceMin)
                .productPriceMax(productPriceMax)
                .productSchedule(productSchedule)
                .qtyMin(qtyMin)
                .qtyMax(qtyMax)
                .tax(tax)
                .transactionDateTimeMin(transactionDateTimeMin)
                .transactionDateTimeMax(transactionDateTimeMax)
                .watchDateMin(watchDateMin)
                .watchDateMax(watchDateMax)
                .paymentStatus(paymentStatus)
                .paymentDateTimeMin(paymentDateTimeMin)
                .paymentDateTimeMax(paymentDateTimeMax)
                .paymentMethod(paymentMethod)
                .seats(seats)
                .createdAtMin(createdAtMin)
                .createdAtMax(createdAtMax)
                .updatedAtMin(updatedAtMin)
                .updatedAtMax(updatedAtMax)
                .expirationDateMin(expirationDateMin)
                .expirationDateMax(expirationDateMax)
                .build();
            Page<TransactionResponse> responsePage = transactionService.getAll(request);
            CommonResponse<List<TransactionResponse>> response = CommonResponse.<List<TransactionResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_TRANSACTION_SUCCESS)
                .data(responsePage.getContent())
                .paging(PagingUtils.pageToPagingResponse(responsePage))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<List<TransactionResponse>> response = CommonResponse.<List<TransactionResponse>>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<TransactionResponse>> updatePaymentTransaction(
        @PathVariable String id,
        @Valid
        @RequestBody
        NewTransactionRequest transactionRequest,
        HttpServletRequest httpServletRequest,
        BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                    String message = fieldError != null
                        ? fieldError.getDefaultMessage()
                        : bindingResult.getAllErrors().get(0).getDefaultMessage();
    
                CommonResponse<TransactionResponse> response = CommonResponse.<TransactionResponse>builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(message)
                        .data(null)
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            CommonResponse<TransactionResponse> response = CommonResponse.<TransactionResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.UPDATE_TRANSACTION_SUCCESS)
                .data(transactionService.updatePaymentStatus(transactionRequest, id, httpServletRequest))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<TransactionResponse> response = CommonResponse.<TransactionResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
