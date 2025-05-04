package com.flix.flix.service;

import org.springframework.data.domain.Page;

import com.flix.flix.entity.Customer;
import com.flix.flix.model.request.NewCustomerRequest;
import com.flix.flix.model.request.UpdateCustomerRequest;
import com.flix.flix.model.request.search.SearchCustomerRequest;
import com.flix.flix.model.response.CustomerResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface CustomerService {

    CustomerResponse create(NewCustomerRequest newCustomerRequest);
    Page<CustomerResponse> getAll(SearchCustomerRequest searchCustomerRequest);
    Customer getCustomerById(String id);
    CustomerResponse getById(String id);
    CustomerResponse update(String id, UpdateCustomerRequest UpdateCustomerRequest);
    CustomerResponse updateByCredentials(HttpServletRequest httpServletRequest, UpdateCustomerRequest UpdateCustomerRequest);
    void delete(String id);

}
