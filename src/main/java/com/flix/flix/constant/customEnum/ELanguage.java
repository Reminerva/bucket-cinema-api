package com.flix.flix.constant.customEnum;

import lombok.Getter;

@Getter
public enum ELanguage {

    ENGLISH("English"),
    INDONESIAN("Indonesian"),
    SPANISH("Spanish"),
    FRENCH("French"),
    GERMAN("German"),
    ITALIAN("Italian"),
    DUTCH("Dutch"),
    PORTUGUESE("Portuguese"),
    RUSSIAN("Russian"),
    CHINESE("Chinese"),
    JAPANESE("Japanese"),
    KOREAN("Korean"),
    ARABIC("Arabic"),
    HINDI("Hindi"),
    TURKISH("Turkish"),
    SWEDISH("Swedish"),
    NORWEGIAN("Norwegian"),
    DANISH("Danish"),
    FINNISH("Finnish"),
    GREEK("Greek"),
    HUNGARIAN("Hungarian"),
    POLISH("Polish"),
    THAI("Thai"),
    VIETNAMESE("Vietnamese"),
    MALAY("Malay"),
    FILIPINO("Filipino"),
    HEBREW("Hebrew"),
    UKRAINIAN("Ukrainian"),
    CZECH("Czech"),
    ROMANIAN("Romanian"),
    BULGARIAN("Bulgarian"),
    SERBIAN("Serbian"),
    CROATIAN("Croatian"),
    SLOVAK("Slovak"),
    LITHUANIAN("Lithuanian"),
    LATVIAN("Latvian"),
    ESTONIAN("Estonian"),
    PERSIAN("Persian"),
    URDU("Urdu"),
    BENGALI("Bengali");

    private final String description;

    ELanguage(String description) {
        this.description = description;
    }

    public static ELanguage findByDescription(String description) {
        for (ELanguage language : values()) {
            if (language.description.equalsIgnoreCase(description)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Invalid language description: " + description);
    }
}
