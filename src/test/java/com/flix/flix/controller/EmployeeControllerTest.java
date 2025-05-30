package com.flix.flix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flix.flix.constant.ApiBash;
import com.flix.flix.global_exception_handler.GlobalExceptionHandler;
import com.flix.flix.model.request.NewAdminRequest;
import com.flix.flix.model.request.NewCashierRequest;
import com.flix.flix.model.request.NewEmployeeRequest;
import com.flix.flix.model.request.UpdateEmployeeRequest;
import com.flix.flix.model.request.search.SearchEmployeeRequest;
import com.flix.flix.model.response.EmployeeResponse;
import com.flix.flix.model.response.SignupResponse;
import com.flix.flix.service.EmployeeService;

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

class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private NewEmployeeRequest newEmployeeRequest;
    private NewAdminRequest newAdminRequest;
    private NewCashierRequest newCashierRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        newEmployeeRequest = new NewEmployeeRequest();
        newEmployeeRequest.setFullname("fullname");
        newEmployeeRequest.setNikNumber("1111111111111111");
        newEmployeeRequest.setAddress("address");
        newEmployeeRequest.setDateOfBirth("dateOfBirth");
        newEmployeeRequest.setCity("city");
        newEmployeeRequest.setPhoneNumber("phoneNumber");
        newEmployeeRequest.setGender("gender");
        newEmployeeRequest.setEmail("email@email.com");
        newEmployeeRequest.setPassword("password");
        newEmployeeRequest.setUsername("username");
        newEmployeeRequest.setTheaterId("theaterId");
        newEmployeeRequest.setDateOfAppliment("dateOfAppliment");

        newAdminRequest = new NewAdminRequest();
        newAdminRequest.setEmail("email@email.com");
        newAdminRequest.setPassword("password");
        newAdminRequest.setUsername("username");

        newCashierRequest = new NewCashierRequest();
        newCashierRequest.setEmail("email@email.com");
        newCashierRequest.setPassword("password");
        newCashierRequest.setUsername("username");
        newCashierRequest.setTheaterId("theaterId");
    }

    @Test
    void testCreateEmployee() throws Exception {

        when(employeeService.create(any(NewEmployeeRequest.class))).thenReturn(new EmployeeResponse());

        mockMvc.perform(post(ApiBash.EMPLOYEE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.CREATE_EMPLOYEE_SUCCESS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));

        verify(employeeService).create(any(NewEmployeeRequest.class));
    }

    @Test
    void testCreateAdmin() throws Exception {

        when(employeeService.createAdmin(any(NewAdminRequest.class))).thenReturn(new SignupResponse());

        mockMvc.perform(post(ApiBash.EMPLOYEE + "/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdminRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.CREATE_ADMIN_SUCCESS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));

        verify(employeeService).createAdmin(any(NewAdminRequest.class));
    }

    @Test
    void testCreateCashier() throws Exception {

        when(employeeService.createCashier(any(NewCashierRequest.class))).thenReturn(new EmployeeResponse());

        mockMvc.perform(post(ApiBash.EMPLOYEE + "/cashier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCashierRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.CREATE_CASHIER_SUCCESS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));

        verify(employeeService).createCashier(any(NewCashierRequest.class));
    }

    @Test
    void testGetAll() throws Exception {
        when(employeeService.getAll(any(SearchEmployeeRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.EMPLOYEE))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_EMPLOYEE_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(employeeService).getAll(any(SearchEmployeeRequest.class));
    }

    @Test
    void testUpdate() throws Exception {
        when(employeeService.update(any(String.class), any(UpdateEmployeeRequest.class))).thenReturn(new EmployeeResponse());

        mockMvc.perform(put(ApiBash.EMPLOYEE + "/{id}", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.UPDATE_EMPLOYEE_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(employeeService).update(any(String.class), any(UpdateEmployeeRequest.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(employeeService).softDelete(any(String.class));

        mockMvc.perform(delete(ApiBash.EMPLOYEE + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.DELETE_EMPLOYEE_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(employeeService).softDelete(any(String.class));
    }

    @Test
    void testGetById() throws Exception {
        when(employeeService.getById(any(String.class))).thenReturn(new EmployeeResponse());

        mockMvc.perform(get(ApiBash.EMPLOYEE + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_EMPLOYEE_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(employeeService).getById(any(String.class));
    }

    @Test
    void testGetMe() throws Exception {
        when(employeeService.getByCredentials(any(HttpServletRequest.class))).thenReturn(new EmployeeResponse()).thenReturn(new EmployeeResponse());

        mockMvc.perform(get(ApiBash.EMPLOYEE + "/me"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_EMPLOYEE_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(employeeService).getByCredentials(any(HttpServletRequest.class));
    }

    @Test
    void testUpdateMe() throws Exception {
        when(employeeService.updateByCredentials(any(UpdateEmployeeRequest.class), any(HttpServletRequest.class))).thenReturn(new EmployeeResponse());

        mockMvc.perform(put(ApiBash.EMPLOYEE + "/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.UPDATE_EMPLOYEE_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(employeeService).updateByCredentials(any(UpdateEmployeeRequest.class), any(HttpServletRequest.class));
    }

}
