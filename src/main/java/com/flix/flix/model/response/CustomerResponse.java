package com.flix.flix.model.response;

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
public class CustomerResponse {

    private String id;
    private String fullname;
    private String country;
    private String phoneNumber;
    private String city;
    private String gender;
    private String registrationDate;
    private String lastLogin;
    private List<String> favGenre;
    private List<String> likeProductId;
    private List<String> dislikeProductId;
    
}
