package com.flix.flix.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewProductionCompanyRequest {

    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "logo url is required")
    private String logoUrl;
    @NotBlank(message = "origin country is required")
    private String originCountry;
    @NotBlank(message = "website url is required")
    private String websiteUrl;
    @NotBlank(message = "headquarters is required")
    private String headquarters;
    @NotBlank(message = "ceo is required")
    private String ceo;
    @NotBlank(message = "description is required")
    private String description;
    @NotBlank(message = "contact email is required")
    private String contactEmail;
    @NotBlank(message = "contact number is required")
    private String contactNumber;
    @NotBlank(message = "founded year is required")
    private String foundedYear;
    
}
