package com.flix.flix.constant.custom_enum;

import lombok.Getter;

@Getter
public enum ELanguage {

    LANGUAGE_ENGLISH("English"),
    LANGUAGE_INDONESIAN("Indonesian"),
    LANGUAGE_SPANISH("Spanish"),
    LANGUAGE_FRENCH("French"),
    LANGUAGE_GERMAN("German"),
    LANGUAGE_ITALIAN("Italian"),
    LANGUAGE_DUTCH("Dutch"),
    LANGUAGE_PORTUGUESE("Portuguese"),
    LANGUAGE_RUSSIAN("Russian"),
    LANGUAGE_CHINESE("Chinese"),
    LANGUAGE_JAPANESE("Japanese"),
    LANGUAGE_KOREAN("Korean"),
    LANGUAGE_ARABIC("Arabic"),
    LANGUAGE_HINDI("Hindi"),
    LANGUAGE_TURKISH("Turkish"),
    LANGUAGE_SWEDISH("Swedish"),
    LANGUAGE_NORWEGIAN("Norwegian"),
    LANGUAGE_DANISH("Danish"),
    LANGUAGE_FINNISH("Finnish"),
    LANGUAGE_GREEK("Greek"),
    LANGUAGE_HUNGARIAN("Hungarian"),
    LANGUAGE_POLISH("Polish"),
    LANGUAGE_THAI("Thai"),
    LANGUAGE_VIETNAMESE("Vietnamese"),
    LANGUAGE_MALAY("Malay"),
    LANGUAGE_FILIPINO("Filipino"),
    LANGUAGE_HEBREW("Hebrew"),
    LANGUAGE_UKRAINIAN("Ukrainian"),
    LANGUAGE_CZECH("Czech"),
    LANGUAGE_ROMANIAN("Romanian"),
    LANGUAGE_BULGARIAN("Bulgarian"),
    LANGUAGE_SERBIAN("Serbian"),
    LANGUAGE_CROATIAN("Croatian"),
    LANGUAGE_SLOVAK("Slovak"),
    LANGUAGE_LITHUANIAN("Lithuanian"),
    LANGUAGE_LATVIAN("Latvian"),
    LANGUAGE_ESTONIAN("Estonian"),
    LANGUAGE_PERSIAN("Persian"),
    LANGUAGE_URDU("Urdu"),
    LANGUAGE_BENGALI("Bengali");

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
