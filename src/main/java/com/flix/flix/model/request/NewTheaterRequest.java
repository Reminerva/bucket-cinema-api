package com.flix.flix.model.request;

import java.util.ArrayList;
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
@NoArgsConstructor
@AllArgsConstructor
public class NewTheaterRequest {

    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "city is required")
    private String city;
    @NotBlank(message = "address is required")
    private String address;
    @NotBlank(message = "contact number is required")
    private String contactNumber;
    @NotBlank(message = "contact email is required")
    private String contactEmail;
    @Builder.Default
    private Boolean oprationalStatus = true;
    @Builder.Default
    private List<String> studiosId = new ArrayList<>();
    @Builder.Default
    private List<String> nowShowingId = new ArrayList<>();
}