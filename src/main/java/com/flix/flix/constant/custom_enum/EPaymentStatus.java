package com.flix.flix.constant.custom_enum;

import lombok.Getter;

@Getter
public enum EPaymentStatus {

    PAYMENT_STATUS_PENDING("Pending"),
    PAYMENT_STATUS_SUCCESS("Success"),
    PAYMENT_STATUS_FAILED("Failed");

    private final String description;

    EPaymentStatus (String description){
        this.description = description;
    }

    public static String getValidPaymentStatuses() {
        StringBuilder validPaymentStatuses = new StringBuilder();
        for (EPaymentStatus paymentStatus : EPaymentStatus.values()) {
            validPaymentStatuses.append(paymentStatus.description).append(", ");
        }
        return validPaymentStatuses.substring(0, validPaymentStatuses.length() - 2);
    }

    public static EPaymentStatus findByDescription(String description) {
        for (EPaymentStatus paymentStatus : EPaymentStatus.values()) {
            if (paymentStatus.description.equalsIgnoreCase(description)) {
                return paymentStatus;
            }
        }
        throw new IllegalArgumentException("Invalid payment status description: '" + description + "'. Valid payment statuses: " + getValidPaymentStatuses());
    }

}
