package com.flix.flix.model.request.search;

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
public class SearchEmployeeRequest {

    private String fullname;
    private String nikNumber;
    private String address;
    private String phoneNumber;
    private String gender;
    private String city;
    private Boolean isActive;
    private String dateOfBirthMin;
    private String dateOfBirthMax;
    private String dateOfApplimentMin;
    private String dateOfApplimentMax;
    private String appUserUsername;
    private String appUserEmail;
    private String theaterName;
    private String theaterCity;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
