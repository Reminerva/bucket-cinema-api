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
@NoArgsConstructor
@AllArgsConstructor
public class TheaterResponse {
    private String id;
    private String name;
    private String city;
    private String address;
    private String contactNumber;
    private String contactEmail;
    private String createdAt;
    private String updatedAt;
    private Boolean oprationalStatus;
    private List<StudioResponse> studios;
}
