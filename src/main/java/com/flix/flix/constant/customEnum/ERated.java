package com.flix.flix.constant.customEnum;

import lombok.Getter;

@Getter
public enum ERated {

    RATEDD_G("G"),
    RATEDD_PG("PG"),
    RATEDD_PG_13("PG-13"),
    RATEDD_R("R"),
    RATEDD_NC_17("NC-17");

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
