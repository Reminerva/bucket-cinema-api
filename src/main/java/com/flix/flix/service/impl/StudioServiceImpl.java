package com.flix.flix.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.customEnum.ESeat;
import com.flix.flix.constant.customEnum.EStudioSize;
import com.flix.flix.entity.ProductPricingScheduling;
import com.flix.flix.entity.Studio;
import com.flix.flix.model.request.NewProductPricingSchedulingRequest;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.response.ProductPricingSchedulingResponse;
import com.flix.flix.model.response.StudioResponse;
import com.flix.flix.repository.StudioRepository;
import com.flix.flix.service.ProductPricingSchedulingService;
import com.flix.flix.service.StudioService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudioServiceImpl implements StudioService {

    private final StudioRepository studioRepository;
    private final ProductPricingSchedulingService productPricingSchedulingService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public StudioResponse create(NewStudioRequest studioRequest) {
        try {
            List<String> availableSeatRequest = studioRequest.getAvailableSeat();

            List<ESeat> availableSeat = ESeat.toESeatList(availableSeatRequest);
            Studio studio = Studio.builder()
                    .name(studioRequest.getName())    
                    .studioSize(EStudioSize.findByDescription(studioRequest.getStudioSize()))
                    .availableSeat(availableSeat)
                    .build();

            return toStudioResponse(studioRepository.saveAndFlush(studio));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<StudioResponse> getAll() {
        try {
            return studioRepository.findAll().stream().map(this::toStudioResponse).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StudioResponse getById(String id) {
        try {
            return toStudioResponse(getStudioById(id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Studio getStudioById(String id) {
        try {
            Optional<Studio> studio = studioRepository.findById(id);
            if (studio.isEmpty()) throw new RuntimeException(DbBash.STUDIO_NOT_FOUND);
            return studio.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public StudioResponse update(String id, NewStudioRequest studioRequest) {
        try {
            List<String> availableSeatRequest = studioRequest.getAvailableSeat();
            List<String> bookedListRequest = studioRequest.getBookedSeat();

            List<ESeat> availableSeat = ESeat.toESeatList(availableSeatRequest);

            List<ESeat> bookedSeat = ESeat.toESeatList(bookedListRequest);

            Studio studio = getStudioById(id);
            studio.setName(studioRequest.getName());
            studio.setStudioSize(EStudioSize.findByDescription(studioRequest.getStudioSize()));
            studio.setAvailableSeat(availableSeat);
            studio.setBookedSeat(bookedSeat);

            List<ProductPricingScheduling> productPricingSchedulings = new ArrayList<>();
            for (NewProductPricingSchedulingRequest productPricingSchedulingRequest : studioRequest.getProductPricingScheduling()) {
                ProductPricingScheduling productPricingScheduling = productPricingSchedulingService.getProductPricingSchedulingByAttribute(productPricingSchedulingRequest);
    
                if (productPricingScheduling == null) {
                    ProductPricingSchedulingResponse productPricingSchedulingResponse = 
                        productPricingSchedulingService.create(productPricingSchedulingRequest);
                    productPricingScheduling = productPricingSchedulingService.getProductPricingSchedulingById(productPricingSchedulingResponse.getId());
                }
                productPricingSchedulings.add(productPricingScheduling);
                productPricingScheduling.getStudios().add(studio);
                studio.setProductPricingScheduling(productPricingSchedulings);
            }

            return toStudioResponse(studioRepository.saveAndFlush(studio));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(String id) {
        try {
            getStudioById(id);
            studioRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StudioResponse toStudioResponse(Studio studio) {
        List<ProductPricingSchedulingResponse> productPricingSchedulingResponses = new ArrayList<>();
        if (studio.getProductPricingScheduling() != null) {
            for (ProductPricingScheduling productPricingScheduling : studio.getProductPricingScheduling()) {
                productPricingSchedulingService.toProductPricingSchedulingResponse(productPricingScheduling);
                productPricingSchedulingResponses.add(productPricingSchedulingService.toProductPricingSchedulingResponse(productPricingScheduling));
            }
        }

        return StudioResponse.builder()
                .id(studio.getId())
                .name(studio.getName())
                .studioSize(studio.getStudioSize())
                .bookedSeat(studio.getBookedSeat())
                .availableSeat(studio.getAvailableSeat())
                .productPricingScheduling(productPricingSchedulingResponses)
                .build();
    }
    
}
