package com.flix.flix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flix.flix.constant.ApiBash;
import com.flix.flix.global_exception_handler.GlobalExceptionHandler;
import com.flix.flix.model.request.LoginRequest;
import com.flix.flix.model.request.NewCustomerRequest;
import com.flix.flix.model.request.search.SearchAppUserRequest;
import com.flix.flix.model.response.CustomerResponse;
import com.flix.flix.model.response.SigninResponse;
import com.flix.flix.model.response.SignoutResponse;
import com.flix.flix.service.AppUserService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class UserControllerTest {

    @Mock
    private AppUserService userService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private LoginRequest loginRequest;
    private NewCustomerRequest newCustomerRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setEmail("email@email.com");
        loginRequest.setPassword("password");
    
        newCustomerRequest = new NewCustomerRequest();
        newCustomerRequest.setFullname("fullname");
        newCustomerRequest.setBirthDate("birthDate");
        newCustomerRequest.setCountry("country");
        newCustomerRequest.setCity("city");
        newCustomerRequest.setPhoneNumber("phoneNumber");
        newCustomerRequest.setGender("male");
        newCustomerRequest.setEmail("email@email.com");
        newCustomerRequest.setPassword("password");
        newCustomerRequest.setUsername("username");
    }

    @Test
    void testSignup() throws Exception {

        when(customerService.create(any(NewCustomerRequest.class))).thenReturn(new CustomerResponse());

        mockMvc.perform(post(ApiBash.USER + ApiBash.AUTH + ApiBash.SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomerRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(ApiBash.SIGN_UP_SUCCESS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));

        verify(customerService).create(any(NewCustomerRequest.class));
    }

    @Test
    void testSignin() throws Exception {

        when(userService.signin(any(LoginRequest.class))).thenReturn(new SigninResponse());

        mockMvc.perform(post(ApiBash.USER + ApiBash.AUTH + ApiBash.SIGN_IN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.SIGN_IN_SUCCESS))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.code").value(202));

        verify(userService).signin(any(LoginRequest.class));
    }

    @Test
    void testGetAll() throws Exception {
        when(userService.getAll(any(SearchAppUserRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.USER))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_USER_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).getAll(any(SearchAppUserRequest.class));
    }

    @Test
    void testSignout() throws Exception {
        when(userService.signout(any(HttpServletRequest.class))).thenReturn(any(SignoutResponse.class));

        mockMvc.perform(post(ApiBash.USER + ApiBash.AUTH + ApiBash.SIGN_OUT))
                .andExpect(jsonPath("$.message").value(ApiBash.SIGN_OUT_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).signout(any(HttpServletRequest.class));
    }
}
