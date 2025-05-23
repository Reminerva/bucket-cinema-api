package com.flix.flix.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EGender;
import com.flix.flix.entity.AppUser;
import com.flix.flix.entity.Employee;
import com.flix.flix.model.request.NewAdminRequest;
import com.flix.flix.model.request.NewCashierRequest;
import com.flix.flix.model.request.NewEmployeeRequest;
import com.flix.flix.model.request.NewUserRequest;
import com.flix.flix.model.request.search.SearchEmployeeRequest;
import com.flix.flix.model.response.EmployeeResponse;
import com.flix.flix.model.response.SignupResponse;
import com.flix.flix.repository.EmployeeRepository;
import com.flix.flix.service.AppUserService;
import com.flix.flix.service.EmployeeService;
import com.flix.flix.service.TheaterService;
import com.flix.flix.specification.EmployeeSpecification;
import com.flix.flix.util.DateUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TheaterService theaterService;
    private final AppUserService appUserService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public EmployeeResponse create(NewEmployeeRequest employeeRequest) {

        try {
            NewUserRequest userRequest = NewUserRequest.builder()
                    .email(employeeRequest.getEmail())
                    .password(employeeRequest.getPassword())
                    .username(employeeRequest.getUsername())
                    .role(List.of("EMPLOYEE"))
                    .build();
            SignupResponse appUserResponse = appUserService.signup(userRequest);
            AppUser appUser = appUserService.getAppUserById(appUserResponse.getAccountId());
            Employee employee = Employee.builder()
                    .fullname(employeeRequest.getFullname())
                    .nikNumber(employeeRequest.getNikNumber())
                    .address(employeeRequest.getAddress())
                    .phoneNumber(employeeRequest.getPhoneNumber())
                    .gender(EGender.findByDescription(employeeRequest.getGender()))
                    .city(employeeRequest.getCity())
                    .dateOfBirth(DateUtil.parseDate(employeeRequest.getDateOfBirth()))
                    .dateOfAppliment(DateUtil.parseDate(employeeRequest.getDateOfAppliment()))
                    .theater(theaterService.getTheaterById(employeeRequest.getTheaterId()))
                    .isActive(true)
                    .appUser(appUser)
                    .build();
            appUser.setEmployee(employee);
            employeeRepository.saveAndFlush(employee);
            return toEmployeeResponse(employee);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public SignupResponse createAdmin(NewAdminRequest adminRequest) {

        try {
            NewUserRequest userRequest = NewUserRequest.builder()
                    .email(adminRequest.getEmail())
                    .password(adminRequest.getPassword())
                    .username(adminRequest.getUsername())
                    .role(List.of("ADMIN"))
                    .build();
            SignupResponse signupResponse = appUserService.signup(userRequest);
            // Employee employee = Employee.builder()
            //         .fullname(null)
            //         .nikNumber(null)
            //         .address(null)
            //         .phoneNumber(null)
            //         .gender(null)
            //         .city(null)
            //         .dateOfBirth(null)
            //         .dateOfAppliment(null)
            //         .theater(theaterService.getTheaterById(theaterId))
            //         .appUser(appUserService.getAppUserById(signupResponse.getAccountId()))
            //         .isActive(true)
            //         .build();
            return signupResponse;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public EmployeeResponse createCashier(NewCashierRequest cashierRequest) {

        try {
            NewUserRequest userRequest = NewUserRequest.builder()
                    .email(cashierRequest.getEmail())
                    .password(cashierRequest.getPassword())
                    .username(cashierRequest.getUsername())
                    .role(List.of("CASHIER"))
                    .build();
            String theaterId = cashierRequest.getTheaterId();
            SignupResponse signupResponse = appUserService.signup(userRequest);
            AppUser appUser = appUserService.getAppUserById(signupResponse.getAccountId());
            Employee employee = Employee.builder()
                    .fullname(null)
                    .nikNumber(null)
                    .address(null)
                    .phoneNumber(null)
                    .gender(null)
                    .city(null)
                    .dateOfBirth(null)
                    .dateOfAppliment(null)
                    .theater(theaterService.getTheaterById(theaterId))
                    .appUser(appUser)
                    .isActive(true)
                    .build();
            appUser.setEmployee(employee);
            employeeRepository.saveAndFlush(employee);
            return toEmployeeResponse(employee);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public Employee getEmployeeById(String id) {

        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) throw new RuntimeException(DbBash.EMPLOYEE_NOT_FOUND);
        return employee.get();

    }

    @Override
    public EmployeeResponse getById(String id) {

        try {
            Employee employee = getEmployeeById(id);
            return toEmployeeResponse(employee);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public Page<EmployeeResponse> getAll(SearchEmployeeRequest searchEmployeeRequest) {

        try {
            if (searchEmployeeRequest.getPage() <= 0) {
                searchEmployeeRequest.setPage(1);
            }
            if (searchEmployeeRequest.getSize() <= 0) {
                searchEmployeeRequest.setSize(10);
            }
            if (searchEmployeeRequest.getDateOfApplimentMin() != null && searchEmployeeRequest.getDateOfApplimentMax() != null) {
                if (DateUtil.parseDate(searchEmployeeRequest.getDateOfApplimentMin()).isAfter(DateUtil.parseDate(searchEmployeeRequest.getDateOfApplimentMax()))) {
                    throw new RuntimeException(DbBash.MIN_MAX_INVALID);
                }
            }
            if (searchEmployeeRequest.getDateOfBirthMin() != null && searchEmployeeRequest.getDateOfBirthMax() != null) {
                if (DateUtil.parseDate(searchEmployeeRequest.getDateOfBirthMin()).isAfter(DateUtil.parseDate(searchEmployeeRequest.getDateOfBirthMax()))) {
                    throw new RuntimeException(DbBash.MIN_MAX_INVALID);
                }
            }
            Sort sort = Sort.by(Sort.Direction.fromString(searchEmployeeRequest.getDirection()), searchEmployeeRequest.getSortBy());
            Pageable pageable = PageRequest.of(searchEmployeeRequest.getPage() - 1, searchEmployeeRequest.getSize(), sort);
            Specification<Employee> specification = EmployeeSpecification.getSpecification(searchEmployeeRequest);
            return employeeRepository.findAll(specification, pageable).map(this::toEmployeeResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public EmployeeResponse update(String id, NewEmployeeRequest employeeRequest) {

        try {
            Employee employee = getEmployeeById(id);
            employee.setFullname(employeeRequest.getFullname());
            employee.setNikNumber(employeeRequest.getNikNumber());
            employee.setAddress(employeeRequest.getAddress());
            employee.setPhoneNumber(employeeRequest.getPhoneNumber());
            employee.setGender(EGender.findByDescription(employeeRequest.getGender()));
            employee.setCity(employeeRequest.getCity());
            employee.setDateOfBirth(DateUtil.parseDate(employeeRequest.getDateOfBirth()));
            employee.setDateOfAppliment(DateUtil.parseDate(employeeRequest.getDateOfAppliment()));
            employee.setTheater(theaterService.getTheaterById(employeeRequest.getTheaterId()));
            employeeRepository.saveAndFlush(employee);
            return toEmployeeResponse(employee);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void softDelete(String id) {

        try {
            Employee employee = getEmployeeById(id);
            employee.setIsActive(false);
            employeeRepository.saveAndFlush(employee);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private EmployeeResponse toEmployeeResponse(Employee employee) {
        try {
            return EmployeeResponse.builder()
                    .id(employee.getId())
                    .fullname(employee.getFullname())
                    .address(employee.getAddress())
                    .city(employee.getCity())
                    .dateOfBirth(employee.getDateOfBirth() == null ? null : employee.getDateOfBirth().toString())
                    .dateOfAppliment(employee.getDateOfAppliment() == null ? null : employee.getDateOfAppliment().toString())
                    .gender(employee.getGender() == null ? null : employee.getGender().toString())
                    .nikNumber(employee.getNikNumber())
                    .appUserId(employee.getAppUser() == null ? null : employee.getAppUser().getId())
                    .phoneNumber(employee.getPhoneNumber())
                    .appUserEmail(employee.getAppUser() == null ? null : employee.getAppUser().getEmail())
                    .appUserUsername(employee.getAppUser() == null ? null : employee.getAppUser().getUsername())
                    .theaterId(employee.getTheater() == null ? null : employee.getTheater().getId())
                    .transactionsId(employee.getTransactions() == null ? null : employee.getTransactions().stream().map(transaction -> transaction.getId()).toList())
                    .isActive(employee.getIsActive())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
