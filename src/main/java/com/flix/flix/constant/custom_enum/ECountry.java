package com.flix.flix.constant.custom_enum;

import lombok.Getter;

@Getter
public enum ECountry {
    COUNTRY_INDONESIA("Indonesia"),
    COUNTRY_ENGLAND("England"),
    COUNTRY_UNITED_STATES("United States"),
    COUNTRY_CANADA("Canada"),
    COUNTRY_AUSTRALIA("Australia"),
    COUNTRY_GERMANY("Germany"),
    COUNTRY_FRANCE("France"),
    COUNTRY_ITALY("Italy"),
    COUNTRY_SPAIN("Spain"),
    COUNTRY_JAPAN("Japan"),
    COUNTRY_CHINA("China"),
    COUNTRY_INDIA("India"),
    COUNTRY_BRAZIL("Brazil"),
    COUNTRY_MEXICO("Mexico"),
    COUNTRY_RUSSIA("Russia"),
    COUNTRY_SOUTH_AFRICA("South Africa"),
    COUNTRY_ARGENTINA("Argentina"),
    COUNTRY_NETHERLANDS("Netherlands"),
    COUNTRY_SWEDEN("Sweden"),
    COUNTRY_SWITZERLAND("Switzerland"),
    COUNTRY_SOUTH_KOREA("South Korea"),
    COUNTRY_SAUDI_ARABIA("Saudi Arabia"),
    COUNTRY_TURKEY("Turkey"),
    COUNTRY_UAE("United Arab Emirates"),
    COUNTRY_EGYPT("Egypt"),
    COUNTRY_THAILAND("Thailand"),
    COUNTRY_SINGAPORE("Singapore"),
    COUNTRY_MALAYSIA("Malaysia"),
    COUNTRY_NEW_ZEALAND("New Zealand"),
    COUNTRY_NORWAY("Norway"),
    COUNTRY_DENMARK("Denmark"),
    COUNTRY_FINLAND("Finland"),
    COUNTRY_POLAND("Poland"),
    COUNTRY_AUSTRIA("Austria"),
    COUNTRY_GREECE("Greece"),
    COUNTRY_PORTUGAL("Portugal"),
    COUNTRY_BELGIUM("Belgium"),
    COUNTRY_IRELAND("Ireland"),
    COUNTRY_CZECH_REPUBLIC("Czech Republic"),
    COUNTRY_HUNGARY("Hungary"),
    COUNTRY_VIETNAM("Vietnam"),
    COUNTRY_PHILIPPINES("Philippines"),
    COUNTRY_PAKISTAN("Pakistan"),
    COUNTRY_BANGLADESH("Bangladesh"),
    COUNTRY_COLOMBIA("Colombia"),
    COUNTRY_PERU("Peru"),
    COUNTRY_CHILE("Chile"),
    COUNTRY_VENEZUELA("Venezuela"),
    COUNTRY_NIGERIA("Nigeria"),
    COUNTRY_KENYA("Kenya"),
    COUNTRY_ISRAEL("Israel"),
    COUNTRY_QATAR("Qatar"),
    COUNTRY_KUWAIT("Kuwait"),
    COUNTRY_IRAN("Iran"),
    COUNTRY_UKRAINE("Ukraine"),
    COUNTRY_BULGARIA("Bulgaria"),
    COUNTRY_ROMANIA("Romania"),
    COUNTRY_SERBIA("Serbia"),
    COUNTRY_CROATIA("Croatia"),
    COUNTRY_SLOVAKIA("Slovakia"),
    COUNTRY_SLOVENIA("Slovenia"),
    COUNTRY_LUXEMBOURG("Luxembourg"),
    COUNTRY_ICELAND("Iceland"),
    COUNTRY_COSTA_RICA("Costa Rica"),
    COUNTRY_URUGUAY("Uruguay"),
    COUNTRY_PARAGUAY("Paraguay"),
    COUNTRY_BOLIVIA("Bolivia");

    private final String description;

    ECountry(String description) {
        this.description = description;
    }

    public static ECountry findByDescription(String description) {
        for (ECountry country : values()) {
            if (country.description.equalsIgnoreCase(description)) {
                return country;
            }
        }
        throw new IllegalArgumentException("Invalid country description: " + description);
    }
}