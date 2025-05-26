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
public class SearchStudioRequest {

    private String name;
    private Boolean isActive;
    private String studioSize;
    private String productId;
    private String theaterId;
    private String theaterName;
    private String theaterCity;
    private List<String> seatLayout;

    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 10;
    @Builder.Default
    private String sortBy = "name";
    @Builder.Default
    private String direction = "asc";
}
