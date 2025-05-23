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
public class SearchProductionCompanyRequest {

    private String name;
    private String originCountry;
    private String foundedYearMin;
    private String foundedYearMax;
    private String headquarters;
    private String ceo;
    private String createdAtMin;
    private String createdAtMax;
    private String updatedAtMin;
    private String updatedAtMax;
    private List<String> hasProducts;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
