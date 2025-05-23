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
    private String theaterName;
    private String theaterCity;
    private List<String> seatLayout;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
