package com.flix.flix.service;

import org.springframework.data.domain.Page;

import com.flix.flix.entity.Employee;
import com.flix.flix.model.request.NewAdminRequest;
import com.flix.flix.model.request.NewCashierRequest;
import com.flix.flix.model.request.NewEmployeeRequest;
import com.flix.flix.model.request.UpdateEmployeeRequest;
import com.flix.flix.model.request.search.SearchEmployeeRequest;
import com.flix.flix.model.response.EmployeeResponse;
import com.flix.flix.model.response.SignupResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface EmployeeService {

    EmployeeResponse create(NewEmployeeRequest employeeRequest);
    EmployeeResponse createCashier(NewCashierRequest cashierRequest);
    SignupResponse createAdmin(NewAdminRequest adminRequest);
    Employee getEmployeeById(String id);
    EmployeeResponse getById(String id);
    Page<EmployeeResponse> getAll(SearchEmployeeRequest searchEmployeeRequest);
    EmployeeResponse update(String id, UpdateEmployeeRequest employeeRequest);
    EmployeeResponse getByCredentials(HttpServletRequest httpServletRequest);
    EmployeeResponse updateByCredentials(UpdateEmployeeRequest employeeRequest, HttpServletRequest httpServletRequest);
    void softDelete(String id);

}
