package com.flix.flix.constant.custom_enum;

import lombok.Getter;

@Getter
public enum EStudioSize {

    STUDIO_REGULER_SMALL("Small Reguler", "A1-L16"),
    STUDIO_REGULER_MEDIUM("Medium Reguler", "A1-N22"),
    STUDIO_REGULER_LARGE("Large Reguler", "A1-R38");

    private final String description;
    private final String seat;

    EStudioSize (String description, String seat){
        this.description = description;
        this.seat = seat;
    }

    public static EStudioSize findByDescription(String description) {
        for (EStudioSize studio : values()) {
            if (studio.description.equalsIgnoreCase(description)) {
                return studio;
            }
        }
        throw new IllegalArgumentException("Invalid studio description: " + description);
    }

    public static EStudioSize findBySeat(String seat) {
        for (EStudioSize studio : values()) {
            if (studio.seat.equalsIgnoreCase(seat)) {
                return studio;
            }
        }
        throw new IllegalArgumentException("Invalid studio seat: " + seat);
    }
}