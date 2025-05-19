package com.flix.flix.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.DbBash;
import com.flix.flix.model.request.NewAdminRequest;
import com.flix.flix.model.request.NewCashierRequest;
import com.flix.flix.model.request.NewEmployeeRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.EmployeeResponse;
import com.flix.flix.model.response.SignupResponse;
import com.flix.flix.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiBash.EMPLOYEE)
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<CommonResponse<EmployeeResponse>> createEmployee(
        @Valid @RequestBody NewEmployeeRequest employeeRequest,
        BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                String message = fieldError != null
                    ? fieldError.getDefaultMessage()
                    : bindingResult.getAllErrors().get(0).getDefaultMessage();

                CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message)
                    .data(null)
                    .paging(null)
                    .build();
            
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_EMPLOYEE_SUCCESS)
                .data(employeeService.create(employeeRequest))
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            String message = e.getMessage();
            if (e.getMessage().contains(DbBash.NIK_NUMBER_ALREADY_EXISTS_CONSTRAINT)) {message = (DbBash.NIK_NUMBER_ALREADY_EXISTS);};
            CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.CREATE_EMPLOYEE_FAILED + ": " + message)
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<CommonResponse<SignupResponse>> createAdmin(
        @Valid @RequestBody NewAdminRequest adminRequest,
        BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                String message = fieldError != null
                    ? fieldError.getDefaultMessage()
                    : bindingResult.getAllErrors().get(0).getDefaultMessage();

                CommonResponse<SignupResponse> response = CommonResponse.<SignupResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message)
                    .data(null)
                    .paging(null)
                    .build();
            
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            CommonResponse<SignupResponse> response = CommonResponse.<SignupResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_ADMIN_SUCCESS)
                .data(employeeService.createAdmin(adminRequest))
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            String message = e.getMessage();
            if (e.getMessage().contains(DbBash.EMAIL_ALREADY_EXISTS_CONSTRAINT)) {message = (DbBash.EMAIL_ALREADY_EXISTS);};
            if (e.getMessage().contains(DbBash.USERNAME_ALREADY_EXISTS_CONSTRAINT)) {message = (DbBash.USERNAME_ALREADY_EXISTS);};
            CommonResponse<SignupResponse> response = CommonResponse.<SignupResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.CREATE_ADMIN_FAILED + ": " + message)
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/cashier")
    public ResponseEntity<CommonResponse<EmployeeResponse>> createCashier(
        @Valid @RequestBody NewCashierRequest cashierRequest,
        BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                String message = fieldError != null
                    ? fieldError.getDefaultMessage()
                    : bindingResult.getAllErrors().get(0).getDefaultMessage();

                CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message)
                    .data(null)
                    .paging(null)
                    .build();
            
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_CASHIER_SUCCESS)
                .data(employeeService.createCashier(cashierRequest))
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            String message = e.getMessage();
            if (e.getMessage().contains(DbBash.EMAIL_ALREADY_EXISTS_CONSTRAINT)) {message = (DbBash.EMAIL_ALREADY_EXISTS);};
            if (e.getMessage().contains(DbBash.USERNAME_ALREADY_EXISTS_CONSTRAINT)) {message = (DbBash.USERNAME_ALREADY_EXISTS);};
            CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.CREATE_CASHIER_FAILED + ": " + message)
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<EmployeeResponse>>> getAll() {
        try {
            CommonResponse<List<EmployeeResponse>> response = CommonResponse.<List<EmployeeResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_EMPLOYEE_SUCCESS)
                .data(employeeService.getAll())
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<List<EmployeeResponse>> response = CommonResponse.<List<EmployeeResponse>>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.GET_ALL_EMPLOYEE_FAILED + ": " + e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<EmployeeResponse>> getById(@PathVariable String id) {
        try {
            CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_EMPLOYEE_SUCCESS)
                .data(employeeService.getById(id))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.GET_EMPLOYEE_FAILED + ": " + e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<EmployeeResponse>> update(
        @PathVariable String id, 
        @Valid @RequestBody NewEmployeeRequest employeeRequest,
        BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                String message = fieldError != null
                    ? fieldError.getDefaultMessage()
                    : bindingResult.getAllErrors().get(0).getDefaultMessage();

                CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message)
                    .data(null)
                    .paging(null)
                    .build();
            
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.UPDATE_EMPLOYEE_SUCCESS)
                .data(employeeService.update(id, employeeRequest))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.UPDATE_EMPLOYEE_FAILED + ": " + e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<EmployeeResponse>> delete(@PathVariable String id) {
        try {
            employeeService.softDelete(id);
            CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.DELETE_EMPLOYEE_SUCCESS)
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.DELETE_EMPLOYEE_FAILED + ": " + e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
