package com.flix.flix.service;

import org.springframework.data.domain.Page;

import com.flix.flix.entity.Transaction;
import com.flix.flix.model.request.NewTransactionRequest;
import com.flix.flix.model.request.search.SearchTransactionRequest;
import com.flix.flix.model.response.TransactionResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface TransactionService {
    TransactionResponse create(NewTransactionRequest transactionRequest, HttpServletRequest request);
    Page<TransactionResponse> getAll(SearchTransactionRequest searchTransactionRequest);
    Page<TransactionResponse> getAllByCredentials(SearchTransactionRequest searchTransactionRequest, HttpServletRequest httpServletRequest);
    Transaction getTransactionById(String id);
    TransactionResponse getById(String id);
    TransactionResponse updatePaymentStatus(NewTransactionRequest transactionRequest, String id, HttpServletRequest request);
}
