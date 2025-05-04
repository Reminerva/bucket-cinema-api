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
public class SearchCustomerRequest {

    private String fullname;
    private String country;
    private String phoneNumber;
    private String city;
    private String gender;
    private String registrationDate;
    private String lastLogin;
    private List<String> favGenres;
    private String email;
    private List<String> likeProductTitle;
    private List<String> dislikeProductTitle;
    private List<String> theaterName;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
