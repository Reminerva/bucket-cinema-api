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
public class SearchTheaterRequest {

    private String name;
    private String city;
    private String address;
    private String contactNumber;
    private String contactEmail;
    private String createdAtMin;
    private String createdAtMax;
    private String updatedAtMin;
    private String updatedAtMax;
    private Boolean oprationalStatus;
    private List<String> employeesName;
    private List<String> productsTitle;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}

