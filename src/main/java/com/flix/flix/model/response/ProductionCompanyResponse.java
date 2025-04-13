package com.flix.flix.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductionCompanyResponse {
    
    private String id;
    private String name;
    private String logoUrl;
    private String originCountry;
    private String websiteUrl;    
    private String headquarters;    
    private String ceo;
    private String description;
    private String contactEmail;
    private String contactNumber;
    private String foundedYear;
    private String createdAt;
    private String updatedAt;
}
