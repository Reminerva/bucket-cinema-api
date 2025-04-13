package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.AppUser;
import com.flix.flix.entity.Customer;
import com.flix.flix.model.request.NewCustomerRequest;
import com.flix.flix.model.response.CustomerResponse;

public interface CustomerService {

    CustomerResponse create(AppUser appUser, NewCustomerRequest newCustomerRequest);
    List<CustomerResponse> getAll();
    Customer getCustomerById(String id);
    CustomerResponse getById(String id);
    CustomerResponse update(String id, NewCustomerRequest newCustomerRequest);
    void delete(String id);

}
