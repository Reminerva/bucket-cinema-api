package com.flix.flix.global_exception_handler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.response.CommonResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handler untuk HttpMessageNotReadableException (termasuk JSON parse error)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<List<Object>>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = ApiBash.INVALID_REQUEST_BODY + ": ";

        // Lebih spesifik untuk MismatchedInputException (biasanya terjadi ketika body kosong)
        if (ex.getCause() instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) ex.getCause();
            if (mie.getMessage().contains("No content to map due to end-of-input")) {
                errorMessage += "Request body is empty or malformed. Please provide a valid JSON object (e.g., {} for empty).";
            } else {
                errorMessage += mie.getOriginalMessage();
            }
        } else {
            errorMessage += ex.getMessage();
        }

        CommonResponse<List<Object>> response = CommonResponse.<List<Object>>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(errorMessage)
            .data(Collections.emptyList()) // Data kosong karena ini error
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // --- Penanganan Validasi Request Body (MethodArgumentNotValidException) ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<List<Object>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Ambil error pertama atau field error spesifik
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message;

        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        } else if (!ex.getBindingResult().getAllErrors().isEmpty()) {
            message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        } else {
            message = "Validation failed for request body."; // Fallback message
        }

        CommonResponse<List<Object>> response = CommonResponse.<List<Object>>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ApiBash.INVALID_REQUEST_BODY + ": " + message) // Gunakan pesan umum untuk validasi
            .data(Collections.emptyList())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<List<Object>>> handleRuntimeException(RuntimeException ex) {
        CommonResponse<List<Object>> response = CommonResponse.<List<Object>>builder()
            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("ERROR" + "! " + ex.getMessage())
            .data(Collections.emptyList())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}