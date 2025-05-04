package com.flix.flix.model.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateCustomerRequest {

    @NotBlank(message = "fullname is required")
    private String fullname;
    @NotBlank(message = "country is required")
    private String country;
    @NotBlank(message = "phone number is required")
    private String phoneNumber;
    @NotBlank(message = "city is required")
    private String city;
    @NotBlank(message = "gender is required")
    private String gender;
    private String registrationDate;
    private String lastLogin;
    private List<String> favGenre;
    private List<String> likeProductId;
    private List<String> dislikeProductId;

}
