package com.flix.flix.model.response;

import java.util.List;

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
public class ProductArtistResponse {

    private String id;
    private String productId;
    private String productTitle;
    private String artistId;
    private String artistName;
    private List<String> artistType;
}
