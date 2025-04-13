package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.Transaction;
import com.flix.flix.model.request.TransactionRequest;
import com.flix.flix.model.response.TransactionResponse;

public interface TransactionService {
    TransactionResponse create(TransactionRequest transactionResponse);
    List<TransactionResponse> getAll();
    Transaction getTransactionById(String id);
    TransactionResponse getById(String id);
    TransactionResponse update(TransactionRequest transactionResponse, String id);
}
