package com.flix.flix.model.request.search;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchProductRequest {

    private String title;
    private Long durationMin;
    private Long durationMax;
    private String language;
    private String country;
    private String releaseDateMin;
    private String releaseDateMax;
    private String rated;
    private Long budgetMin;
    private Long budgetMax;
    private Double imdbRatingMin;
    private Double imdbRatingMax;
    private Integer rottenTomatoesRatingMin;
    private Integer rottenTomatoesRatingMax;
    private String directorName;
    private String writerName;
    private String producerName;
    private List<String> movieGenre;
    private Double productPricingMin;
    private Double productPricingMax;
    private String lastUpdatedMin;
    private String lastUpdatedMax;
    private List<String> artistsName;
    private String productionCompany;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
