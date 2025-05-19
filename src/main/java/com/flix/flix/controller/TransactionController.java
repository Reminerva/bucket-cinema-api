package com.flix.flix.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.NewTransactionRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.TransactionResponse;
import com.flix.flix.service.TransactionService;

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
    public ResponseEntity<CommonResponse<List<TransactionResponse>>> getAllTransaction() {
        try {
            CommonResponse<List<TransactionResponse>> response = CommonResponse.<List<TransactionResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_TRANSACTION_SUCCESS)
                .data(transactionService.getAll())
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
    public ResponseEntity<CommonResponse<TransactionResponse>> updateTransaction(
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
                .data(transactionService.update(transactionRequest, id, httpServletRequest))
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
