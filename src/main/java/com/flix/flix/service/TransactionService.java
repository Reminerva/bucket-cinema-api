package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.Transaction;
import com.flix.flix.model.request.NewTransactionRequest;
import com.flix.flix.model.response.TransactionResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface TransactionService {
    TransactionResponse create(NewTransactionRequest transactionRequest, HttpServletRequest request);
    List<TransactionResponse> getAll();
    Transaction getTransactionById(String id);
    TransactionResponse getById(String id);
    TransactionResponse update(NewTransactionRequest transactionRequest, String id, HttpServletRequest request);
}
