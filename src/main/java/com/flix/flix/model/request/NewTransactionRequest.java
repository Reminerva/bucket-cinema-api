package com.flix.flix.model.request;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class NewTransactionRequest {
    @NotBlank(message = "theater id is required")
    private String theaterId;
    @NotBlank(message = "studio id is required")
    private String studioId;
    @NotBlank(message = "product id is required")
    private String productId;
    @NotBlank(message = "product pricing id is required")
    private String productPricingId;
    @NotBlank(message = "product scheduling id is required")
    private String productSchedulingId;
    @Min(value = 0, message = "qty is required")
    private Integer qty;
    @Min(value = 0, message = "tax is required")
    @Max(value = 100, message = "tax is required")
    private Integer tax;
    @NotBlank(message = "transaction date time is required")
    private String transactionDateTime;
    @NotBlank(message = "watch date is required")
    private String watchDate;
    @NotBlank(message = "payment date time is required")
    private String paymentDateTime;
    @NotBlank(message = "payment method is required")
    private String paymentMethod;
    private String paymentStatus;
    @NotEmpty(message = "seats is required")
    private List<String> seats;
}
