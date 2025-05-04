package com.flix.flix.constant.custom_enum;

import lombok.Getter;

@Getter
public enum EGender {

    GENDER_MALE("Male"),
    GENDER_FEMALE("Female");

    private final String description;

    EGender (String description){
        this.description = description;
    }

    public static EGender findByDescription(String description){
        for (EGender gender : values()){
            if (gender.description.equalsIgnoreCase(description)){
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid gender description: " + description);
    }
    
}
