package com.flix.flix.constant.custom_enum;

import java.util.ArrayList;
import java.util.List;

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

    public static String getValidTypes() {
        String validTypes = "";
        for (EArtistType type : values()) {
            validTypes += type.description + ", ";
        }
        return validTypes.substring(0, validTypes.length() - 2);
    }

    public static EArtistType findByDescription(String description){
        for (EArtistType type : values()){
            if (type.description.equalsIgnoreCase(description)){
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid artist type description: '" + description + "'. Valid artist types: " + getValidTypes());
    }

    public static List<EArtistType> toEArtistTypeList(List<String> seats) {
        List<EArtistType> seatList = new ArrayList<>();
        for (String seat : seats) {
            seatList.add(EArtistType.findByDescription(seat));
        }
        return seatList;
    }

    public static List<String> toEArtistTypeStringList(List<EArtistType> seats) {
        List<String> seatList = new ArrayList<>();
        for (EArtistType seat : seats) {
            seatList.add(seat.description);
        }
        return seatList;
    }
}