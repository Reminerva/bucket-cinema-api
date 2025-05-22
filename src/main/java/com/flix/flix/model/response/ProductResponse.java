package com.flix.flix.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String title;
    private Long duration;
    private String language;
    private String country;
    private String releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private String rated;
    private Long budget;
    private String synopsis;
    private String tagline;
    private Double imdbRating;
    private Integer rottenTomatoesRating;
    private List<String> actorsId;
    private List<String> directorsId;
    private List<String> producersId;
    private List<String> writersId;
    private List<String> editorsId;
    private List<String> musicDirectorsId;
    private String productionCompanyId;
    private List<String> movieGenre;
    private String lastUpdated;
    private List<String> customerLikeId;
    private List<String> customerDislikeId;
    private List<TheaterResponse> showingOnTheaters;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TheaterResponse {
        private String id;
        private String name;
        private String city;
        private String address;
        private String contactNumber;
        private String contactEmail;
    }
}
