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
public class NewProductArtistRequest {

    @NotBlank(message = "artist id is required")
    private String artistId;
    @NotBlank(message = "product id is required")
    private String productId;
    @NotEmpty(message = "artist type is required")
    private List<String> artistTypes;
}
