package com.flix.flix.constant.custom_enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ETax {

    TAX_0(0),
    TAX_10(10);

    private final Integer value;

    public static String getValidTaxes() {
        String validTaxes = "";
        for (ETax tax : values()) {
            validTaxes += tax.value + ", ";
        }
        return validTaxes.substring(0, validTaxes.length() - 2);
    }

    public static ETax findByValue(Integer value) {
        for (ETax etax : ETax.values()) {
            if (etax.value.equals(value)) {
                return etax;
            }
        }
        throw new IllegalArgumentException("Invalid tax value: '" + value + "'. Valid taxes: " + getValidTaxes());
    }
}
