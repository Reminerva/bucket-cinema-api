package com.flix.flix.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.flix.flix.constant.customEnum.EPaymentMethod;
import com.flix.flix.constant.customEnum.ESeat;
import com.flix.flix.entity.Customer;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductPricing;
import com.flix.flix.entity.ProductPricingScheduling;
import com.flix.flix.entity.Studio;
import com.flix.flix.entity.Theater;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String id;
    private String customerId;
    private String theaterId;
    private String studioId;
    private String productId;
    private String productPricingId;
    private String productSchedulingId;
    private Integer qty;
    private Integer tax;
    private String transactionDate;
    private Boolean paymentStatus;
    private String paymentDateTime;
    private String paymentMethod;
    private List<String> seats;
    private String createdAt;
    private String updatedAt;
}
