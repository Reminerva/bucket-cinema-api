package com.flix.flix.constant.customEnum;

import lombok.Getter;

@Getter
public enum EPaymentMethod {

    PAYMENT_Method_CASH("Cash"),
    PAYMENT_Method_CARD("Card"),
    PAYMENT_Method_PAYPAL("PayPal"),
    PAYMENT_Method_BANK_TRANSFER("Bank Transfer");

    private final String description;

    EPaymentMethod (String description){
        this.description = description;
    }

    public static EPaymentMethod findByDescription(String description) {
        for (EPaymentMethod paymentType : values()) {
            if (paymentType.description.equalsIgnoreCase(description)) {
                return paymentType;
            }
        }
        throw new IllegalArgumentException("Invalid payment type description: " + description);
    }
}
