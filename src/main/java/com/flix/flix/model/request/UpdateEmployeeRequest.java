package com.flix.flix.model.request;

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
public class UpdateEmployeeRequest {

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

}

