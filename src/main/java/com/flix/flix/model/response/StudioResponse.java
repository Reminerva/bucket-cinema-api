package com.flix.flix.model.response;

import java.util.List;

import com.flix.flix.constant.customEnum.ESeat;
import com.flix.flix.constant.customEnum.EStudioSize;

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
public class StudioResponse {
    private String id;
    private String name;
    private EStudioSize studioSize;
    private List<ESeat> bookedSeat;
    private List<ESeat> availableSeat;
    private List<ProductPricingSchedulingResponse> productPricingScheduling;
}
