package com.flix.flix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flix.flix.constant.ApiBash;
import com.flix.flix.global_exception_handler.GlobalExceptionHandler;
import com.flix.flix.model.request.NewCustomerRequest;
import com.flix.flix.model.request.UpdateCustomerRequest;
import com.flix.flix.model.request.search.SearchCustomerRequest;
import com.flix.flix.model.response.CustomerResponse;
import com.flix.flix.service.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private NewCustomerRequest newCustomerRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        newCustomerRequest = new NewCustomerRequest();
        newCustomerRequest.setFullname("fullname");
        newCustomerRequest.setBirthDate("birthDate");
        newCustomerRequest.setCountry("country");
        newCustomerRequest.setCity("city");
        newCustomerRequest.setPhoneNumber("phoneNumber");
        newCustomerRequest.setGender("gender");
        newCustomerRequest.setEmail("email@email.com");
        newCustomerRequest.setPassword("password");
        newCustomerRequest.setUsername("username");
    }

    @Test
    void testCreate() throws Exception {

        when(customerService.create(any(NewCustomerRequest.class))).thenReturn(new CustomerResponse());

        mockMvc.perform(post(ApiBash.CUSTOMER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomerRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.CREATE_CUSTOMER_SUCCESS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));

        verify(customerService).create(any(NewCustomerRequest.class));
    }

    @Test
    void testGetAll() throws Exception {
        when(customerService.getAll(any(SearchCustomerRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.CUSTOMER))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_CUSTOMER_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(customerService).getAll(any(SearchCustomerRequest.class));
    }

    @Test
    void testUpdate() throws Exception {
        when(customerService.update(any(String.class), any(UpdateCustomerRequest.class))).thenReturn(new CustomerResponse());

        mockMvc.perform(put(ApiBash.CUSTOMER + "/{id}", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomerRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.UPDATE_CUSTOMER_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(customerService).update(any(String.class), any(UpdateCustomerRequest.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(customerService).delete(any(String.class));

        mockMvc.perform(delete(ApiBash.CUSTOMER + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.DELETE_CUSTOMER_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(customerService).delete(any(String.class));
    }

    @Test
    void testGetById() throws Exception {
        when(customerService.getById(any(String.class))).thenReturn(new CustomerResponse());

        mockMvc.perform(get(ApiBash.CUSTOMER + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_CUSTOMER_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(customerService).getById(any(String.class));
    }

    @Test
    void testGetMe() throws Exception {
        when(customerService.getByCredentials(any(HttpServletRequest.class))).thenReturn(new CustomerResponse()).thenReturn(new CustomerResponse());

        mockMvc.perform(get(ApiBash.CUSTOMER + "/me"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_CUSTOMER_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(customerService).getByCredentials(any(HttpServletRequest.class));
    }

    @Test
    void testUpdateMe() throws Exception {
        when(customerService.updateByCredentials(any(HttpServletRequest.class), any(UpdateCustomerRequest.class))).thenReturn(new CustomerResponse());

        mockMvc.perform(put(ApiBash.CUSTOMER + "/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomerRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.UPDATE_CUSTOMER_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(customerService).updateByCredentials(any(HttpServletRequest.class), any(UpdateCustomerRequest.class));
    }
}
