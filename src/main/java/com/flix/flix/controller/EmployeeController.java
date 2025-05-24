package com.flix.flix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.flix.flix.model.request.NewAdminRequest;
import com.flix.flix.model.request.NewCashierRequest;
import com.flix.flix.model.request.NewEmployeeRequest;
import com.flix.flix.model.request.search.SearchEmployeeRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.EmployeeResponse;
import com.flix.flix.model.response.SignupResponse;
import com.flix.flix.service.EmployeeService;
import com.flix.flix.util.PagingUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiBash.EMPLOYEE)
public class EmployeeController {

    private final EmployeeService employeeService;

    // admin only //
    @PostMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<EmployeeResponse>> createEmployee(
        @Valid @RequestBody NewEmployeeRequest employeeRequest
    ) {
        CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
            .code(HttpStatus.CREATED.value())
            .message(ApiBash.CREATE_EMPLOYEE_SUCCESS)
            .data(employeeService.create(employeeRequest))
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/admin")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<SignupResponse>> createAdmin(
        @Valid @RequestBody NewAdminRequest adminRequest
    ) {
        CommonResponse<SignupResponse> response = CommonResponse.<SignupResponse>builder()
            .code(HttpStatus.CREATED.value())
            .message(ApiBash.CREATE_ADMIN_SUCCESS)
            .data(employeeService.createAdmin(adminRequest))
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/cashier")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<EmployeeResponse>> createCashier(
        @Valid @RequestBody NewCashierRequest cashierRequest
    ) {
        CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
            .code(HttpStatus.CREATED.value())
            .message(ApiBash.CREATE_CASHIER_SUCCESS)
            .data(employeeService.createCashier(cashierRequest))
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<List<EmployeeResponse>>> getAll(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "fullname") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestParam(required = false) String fullname,
        @RequestParam(required = false) String nikNumber,
        @RequestParam(required = false) String address,
        @RequestParam(required = false) String phoneNumber,
        @RequestParam(required = false) String gender,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) Boolean isActive,
        @RequestParam(required = false) String dateOfBirthMin,
        @RequestParam(required = false) String dateOfBirthMax,
        @RequestParam(required = false) String dateOfApplimentMin,
        @RequestParam(required = false) String dateOfApplimentMax,
        @RequestParam(required = false) String appUserUsername,
        @RequestParam(required = false) String appUserEmail,
        @RequestParam(required = false) String theaterName,
        @RequestParam(required = false) String theaterCity
    ) {
        SearchEmployeeRequest request = SearchEmployeeRequest.builder()
            .page(page)
            .size(size) 
            .sortBy(sortBy)
            .direction(direction)
            .fullname(fullname)
            .nikNumber(nikNumber)
            .address(address)
            .phoneNumber(phoneNumber)
            .gender(gender)
            .city(city)
            .isActive(isActive)
            .dateOfBirthMin(dateOfBirthMin)
            .dateOfBirthMax(dateOfBirthMax)
            .dateOfApplimentMin(dateOfApplimentMin)
            .dateOfApplimentMax(dateOfApplimentMax)
            .appUserUsername(appUserUsername)
            .appUserEmail(appUserEmail)
            .theaterName(theaterName)
            .theaterCity(theaterCity)
            .build();
        Page<EmployeeResponse> employee = employeeService.getAll(request);
        CommonResponse<List<EmployeeResponse>> response = CommonResponse.<List<EmployeeResponse>>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_ALL_EMPLOYEE_SUCCESS)
            .data(employee.getContent())
            .paging(PagingUtils.pageToPagingResponse(employee))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<EmployeeResponse>> getById(
        @PathVariable String id
    ) {
        CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_EMPLOYEE_SUCCESS)
            .data(employeeService.getById(id))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<EmployeeResponse>> update(
        @PathVariable String id, 
        @Valid @RequestBody NewEmployeeRequest employeeRequest
    ) {
        CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.UPDATE_EMPLOYEE_SUCCESS)
            .data(employeeService.update(id, employeeRequest))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<EmployeeResponse>> delete(
        @PathVariable String id
    ) {
        employeeService.softDelete(id);
        CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.DELETE_EMPLOYEE_SUCCESS)
            .data(null)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // employee only //
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<EmployeeResponse>> getMe(
        HttpServletRequest httpServletRequest
    ) {
        CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_EMPLOYEE_SUCCESS)
            .data(employeeService.getByCredentials(httpServletRequest))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/me")
    public ResponseEntity<CommonResponse<EmployeeResponse>> updateMe(
        HttpServletRequest httpServletRequest,
        @Valid @RequestBody NewEmployeeRequest employeeRequest
    ) {
        CommonResponse<EmployeeResponse> response = CommonResponse.<EmployeeResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.UPDATE_EMPLOYEE_SUCCESS)
            .data(employeeService.updateByCredentials(employeeRequest, httpServletRequest))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
