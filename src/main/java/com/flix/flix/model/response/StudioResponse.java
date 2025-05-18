package com.flix.flix.model.response;

import java.util.List;

import com.flix.flix.constant.custom_enum.EStudioSize;

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
    private List<String> seatLayout;
    private List<StudioSeatScheduleResponse> studioSeatSchedule;
    private List<ProductPricingResponse> productPricing;
    private List<ProductSchedulingResponse> productScheduling;
}
