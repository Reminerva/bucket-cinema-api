package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.Employee;
import com.flix.flix.model.request.NewAdminRequest;
import com.flix.flix.model.request.NewCashierRequest;
import com.flix.flix.model.request.NewEmployeeRequest;
import com.flix.flix.model.response.EmployeeResponse;
import com.flix.flix.model.response.SignupResponse;

public interface EmployeeService {

    EmployeeResponse create(NewEmployeeRequest employeeRequest);
    EmployeeResponse createCashier(NewCashierRequest cashierRequest);
    SignupResponse createAdmin(NewAdminRequest adminRequest);
    Employee getEmployeeById(String id);
    EmployeeResponse getById(String id);
    List<EmployeeResponse> getAll();
    EmployeeResponse update(String id, NewEmployeeRequest employeeRequest);
    void softDelete(String id);

}
