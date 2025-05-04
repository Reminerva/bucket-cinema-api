package com.flix.flix.constant.custom_enum;

import lombok.Getter;

@Getter
public enum ERole {
    
    ROLE_ADMIN("Admin"),
    ROLE_CASHIER("Cashier"),
    ROLE_CUSTOMER("Customer");

    private final String description;

    ERole (String description){
        this.description = description;
    }

    public static ERole findByDescription(String description){
        for (ERole role : values()){
            if (role.description.equalsIgnoreCase(description)){
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role description: " + description);
    }
}
