package com.flix.flix.model.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class NewProductRequest {
    @NotBlank(message = "title is required")
    private String title;
    @Min(value = 0, message = "duration is required")
    private Long duration;
    @NotBlank(message = "language is required")
    private String language;
    @NotBlank(message = "country is required")
    private String country;
    @NotBlank(message = "release date is required")
    private String releaseDate;
    @NotBlank(message = "poster url is required")
    private String posterUrl;
    @NotBlank(message = "trailer url is required")
    private String trailerUrl;
    @NotBlank(message = "rated is required")
    private String rated;
    @Min(value = 0, message = "budget is required")
    private Long budget;
    @NotBlank(message = "synopsis is required")
    private String synopsis;
    @NotBlank(message = "tagline is required")
    private String tagline;
    @Builder.Default
    private Double imdbRating = 0.0;
    @Builder.Default
    private Integer rottenTomatoesRating = 0;
    @NotBlank(message = "production company id is required")
    private String productionCompanyId;
    @NotEmpty(message = "movie genre is required")
    private List<String> movieGenre;
    @NotEmpty(message = "artist id is required")
    private List<String> artistId;
    @Builder.Default
    private List<String> showingOnTheaters = new ArrayList<>();
}
