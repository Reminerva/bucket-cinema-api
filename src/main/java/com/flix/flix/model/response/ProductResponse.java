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
    private String director;
    private String writer;
    private String producer;
    private String productionCompany;
    private List<String> movieGenre;
    private String lastUpdated;
    private List<String> artistId;
    private List<String> customerLikeId;
    private List<String> customerDislikeId;
}
