package com.flix.flix.constant.custom_enum;

import lombok.Getter;

@Getter
public enum EGenre {

    GENRE_ACTION("Action"),
    GENRE_COMEDY("Comedy"),
    GENRE_DRAMA("Drama"),
    GENRE_HORROR("Horror"),
    GENRE_ROMANCE("Romance"),
    GENRE_THRILLER("Thriller"),
    GENRE_FANTASY("Fantasy"),
    GENRE_ANIMATION("Animation"),
    GENRE_DOCUMENTARY("Documentary"),
    GENRE_MOCKUMENTARY("Mockumentary"),
    GENRE_SCIENCE("Science"),
    GENRE_BIOGRAPHY("Biography"),
    GENRE_SCIFI("Sci-Fi"),
    GENRE_MYSTERY("Mystery"),
    GENRE_CRIME("Crime"),
    GENRE_FAMILY("Family"),
    GENRE_HISTORY("History"),
    GENRE_WESTERN("Western"),
    GENRE_MUSIC("Music"),
    GENRE_MUSICAL("Musical"),
    GENRE_WAR("War"),
    GENRE_FICTION("Fiction"),
    GENRE_ANIME("Anime"),
    GENRE_OTHER("Other");

    private final String description;

    EGenre (String description){
        this.description = description;
    }

    public static EGenre findByDescription(String description) {
        for (EGenre genre : values()) {
            if (genre.description.equalsIgnoreCase(description)) {
                return genre;
            }
        }
        throw new IllegalArgumentException("Invalid genre description: " + description);
    }
}
