package com.flix.flix.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class NewEmployeeRequest {

    @NotBlank(message = "fullname is required")
    private String fullname;
    @NotBlank(message = "nik number is required")
    @Size(min = 16, max = 16, message = "Nik number must be 16 characters")
    private String nikNumber;
    @NotBlank(message = "address is required")
    private String address;
    @NotBlank(message = "phone number is required")
    private String phoneNumber;
    @NotBlank(message = "gender is required")
    private String gender;
    @NotBlank(message = "city is required")
    private String city;
    @NotBlank(message = "date of birth is required")
    private String dateOfBirth;
    @NotBlank(message = "theater id is required")
    private String theaterId;
    private String dateOfAppliment;

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    @NotBlank(message = "email is required")
    @Email
    private String email;
    @NotBlank(message = "password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;

}
