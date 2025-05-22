package com.flix.flix.model.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class NewArtistRequest {
    
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "place of birth is required")
    private String placeOfBirth;
    @NotBlank(message = "birth date is required")
    private String birthDate;
    private String otherName;
    @NotBlank(message = "bio is required")
    private String bio;
    @NotEmpty(message = "artist type is required")
    private List<String> artistTypes;

}
