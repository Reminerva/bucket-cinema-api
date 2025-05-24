package com.flix.flix.model.request.search;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchTransactionRequest {

    private String customerId;
    private String customerName;
    private String employeeId;
    private String employeeName;
    private String theaterName;
    private String studioName;
    private String productTitle;
    private Double productPriceMin;
    private Double productPriceMax;
    private String productSchedule;
    private Integer qtyMin;
    private Integer qtyMax;
    private Integer tax;
    private String transactionDateTimeMin;
    private String transactionDateTimeMax;
    private String watchDateMin;
    private String watchDateMax;
    private String paymentStatus;
    private String paymentDateTimeMin;
    private String paymentDateTimeMax;
    private String paymentMethod;
    private List<String> seats;
    private String createdAtMin;
    private String createdAtMax;
    private String updatedAtMin;
    private String updatedAtMax;
    private String expirationDateMin;
    private String expirationDateMax;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
