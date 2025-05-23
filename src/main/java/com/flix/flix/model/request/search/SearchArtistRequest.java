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
public class SearchArtistRequest {

    private String name;
    private String placeOfBirth;
    private String birthDateMin;
    private String birthDateMax;
    private List<String> artistType;
    private List<String> inProductTitle;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
