package com.flix.flix.model.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
public class NewCustomerRequest {

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
    @NotBlank(message = "birth date is required")
    private String birthDate;
    private String registrationDate;
    private String lastLogin;
    private List<String> favGenre;
    private List<String> likeProductId;
    private List<String> dislikeProductId;
    @NotBlank(message = "username is required")
    @Size(min = 5, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    @NotBlank(message = "email is required")
    @Email
    private String email;
    @NotBlank(message = "password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;
    @NotEmpty(message = "role is required")
    private List<String> role;

}
