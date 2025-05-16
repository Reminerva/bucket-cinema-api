package com.flix.flix.constant.custom_enum;

import lombok.Getter;

@Getter
public enum ERated {

    RATED_G("G"),
    RATED_PG("PG"),
    RATED_PG_13("PG-13"),
    RATED_R("R"),
    RATED_NC_17("NC-17");

    private final String description;

    ERated (String description){
        this.description = description;
    }

    public static ERated findByDescription(String description){
        for (ERated rated : values()){
            if (rated.description.equalsIgnoreCase(description)){
                return rated;
            }
        }
        throw new IllegalArgumentException("Invalid rated description: " + description);
    }
}
