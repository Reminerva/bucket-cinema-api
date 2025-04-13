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
public class NewProductSchedulingRequest {
    @NotBlank(message = "schedule is required")
    private String schedule;
    @NotBlank(message = "product id is required")
    private String productIdScheduling;
}
