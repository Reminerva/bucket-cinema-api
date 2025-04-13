package com.flix.flix.constant.customEnum;

import lombok.Getter;

@Getter
public enum EArtistType {

    TYPE_DIRECTOR("Director"),
    TYPE_WRITER("Writer"),
    TYPE_ACTOR("Actor"),
    TYPE_PRODUCER("Producer");

    private final String description;

    EArtistType (String description){
        this.description = description;
    }

    public static EArtistType findByDescription(String description){
        for (EArtistType type : values()){
            if (type.description.equalsIgnoreCase(description)){
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid artist type description: " + description);
    }
}