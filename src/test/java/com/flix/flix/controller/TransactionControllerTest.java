package com.flix.flix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flix.flix.constant.ApiBash;
import com.flix.flix.global_exception_handler.GlobalExceptionHandler;
import com.flix.flix.model.request.NewTransactionRequest;
import com.flix.flix.model.request.search.SearchTransactionRequest;
import com.flix.flix.model.response.TransactionResponse;
import com.flix.flix.service.TransactionService;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private NewTransactionRequest newTransactionRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    
        newTransactionRequest = new NewTransactionRequest();
        newTransactionRequest.setSeats(List.of("A1"));
        newTransactionRequest.setQty(1);
        newTransactionRequest.setPaymentDateTime("2023-08-25T10:00:00");
        newTransactionRequest.setPaymentMethod("cash");
        newTransactionRequest.setPaymentStatus("paid");
        newTransactionRequest.setProductId("1");
        newTransactionRequest.setProductPricingId("1");
        newTransactionRequest.setProductSchedulingId("1");
        newTransactionRequest.setStudioId("1");
        newTransactionRequest.setTheaterId("1");
        newTransactionRequest.setTransactionDateTime("2023-08-25T10:00:00");
        newTransactionRequest.setWatchDate("2023-08-25");

    }

    @Test
    void testCreateTransaction() throws Exception {
        when(transactionService.create(any(NewTransactionRequest.class), any(HttpServletRequest.class))).thenReturn(new TransactionResponse());

        mockMvc.perform(post(ApiBash.TRANSACTION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTransactionRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.CREATE_TRANSACTION_SUCCESS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));

        verify(transactionService).create(any(NewTransactionRequest.class), any(HttpServletRequest.class));
    }

    @Test
    void testGetAllMyTransaction() throws Exception {
        when(transactionService.getAllByCredentials(any(SearchTransactionRequest.class), any(HttpServletRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.TRANSACTION + "/me"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_TRANSACTION_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
            
        verify(transactionService).getAllByCredentials(any(SearchTransactionRequest.class), any(HttpServletRequest.class));
    }

    @Test
    void testGetAllTransaction() throws Exception {
        when(transactionService.getAll(any(SearchTransactionRequest.class))).thenReturn(Page.empty());

        mockMvc.perform(get(ApiBash.TRANSACTION))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_ALL_TRANSACTION_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(transactionService).getAll(any(SearchTransactionRequest.class));
    }

    @Test
    void testGetById() throws Exception {
        when(transactionService.getById(any(String.class))).thenReturn(new TransactionResponse());

        mockMvc.perform(get(ApiBash.TRANSACTION + "/{id}", "id"))
                .andExpect(jsonPath("$.message").value(ApiBash.GET_TRANSACTION_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(transactionService).getById(any(String.class));
    }

    @Test
    void testUpdatePaymentTransaction() throws Exception {
        when(transactionService.updatePaymentStatus(any(NewTransactionRequest.class), any(String.class), any(HttpServletRequest.class))).thenReturn(new TransactionResponse());

        mockMvc.perform(put(ApiBash.TRANSACTION + "/{id}", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTransactionRequest)))
                .andExpect(jsonPath("$.message").value(ApiBash.UPDATE_TRANSACTION_SUCCESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(transactionService).updatePaymentStatus(any(NewTransactionRequest.class), any(String.class), any(HttpServletRequest.class));
    }
}
