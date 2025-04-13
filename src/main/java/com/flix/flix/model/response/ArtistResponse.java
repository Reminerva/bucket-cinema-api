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
public class ArtistResponse {
    private String id;
    private String name;
    private String placeOfBirth;
    private String birthDate;
    private String otherName;
    private String bio;
    private String artistType;
}
