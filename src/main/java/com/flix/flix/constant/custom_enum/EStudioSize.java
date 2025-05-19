package com.flix.flix.constant.custom_enum;

import java.util.List;

import lombok.Getter;

@Getter
public enum EStudioSize {

    STUDIO_REGULER_SMALL("Reguler Small", "A1-L16"),
    STUDIO_REGULER_MEDIUM("Reguler Medium", "A1-N22"),
    STUDIO_REGULER_LARGE("Reguler Large", "A1-R38");

    private final String description;
    private final String seat;

    EStudioSize (String description, String seat){
        this.description = description;
        this.seat = seat;
    }

    public static String getValidStudioSize() {
        String validStudios = "";
        for (EStudioSize studio : values()) {
            validStudios += studio.description + ", ";
        }
        return validStudios.substring(0, validStudios.length() - 2);
    }

    public static EStudioSize findByDescription(String description) {
        for (EStudioSize studio : values()) {
            if (studio.description.equalsIgnoreCase(description)) {
                return studio;
            }
        }
        throw new IllegalArgumentException("Invalid studio description: " + description + ". Valid studios: " + getValidStudioSize());
    }

    public static EStudioSize findBySeat(String seat) {
        for (EStudioSize studio : values()) {
            if (studio.seat.equalsIgnoreCase(seat)) {
                return studio;
            }
        }
        throw new IllegalArgumentException("Invalid studio seat: " + seat);
    }

    public static Boolean isSeatValid(List<ESeat> seatList, EStudioSize studio) {
        List<String> seats = List.of(studio.seat.split("-"));
        String seatAlphabetMin = seats.get(0).substring(0, 1);
        String seatAlphabetMax = seats.get(1).substring(0, 1);
        Integer seatNumberMin = Integer.parseInt(seats.get(0).substring(1));
        Integer seatNumberMax = Integer.parseInt(seats.get(1).substring(1));
        
        for (ESeat seat : seatList) {
            if (seat.getDescription().substring(0,1).charAt(0) > seatAlphabetMax.charAt(0) || 
                seat.getDescription().substring(0,1).charAt(0) < seatAlphabetMin.charAt(0)
            ) {
                if (Integer.parseInt(seat.getDescription().substring(1)) > seatNumberMax || 
                    Integer.parseInt(seat.getDescription().substring(1)) < seatNumberMin
                ) {
                    return false;
                }
            }
        }
        return true;
    }
}