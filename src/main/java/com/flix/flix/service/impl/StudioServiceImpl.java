package com.flix.flix.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ESeat;
import com.flix.flix.constant.custom_enum.EStudioSize;
import com.flix.flix.entity.ProductPricing;
import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.entity.Studio;
import com.flix.flix.model.request.NewProductPricingRequest;
import com.flix.flix.model.request.NewProductSchedulingRequest;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.response.ProductPricingResponse;
import com.flix.flix.model.response.ProductSchedulingResponse;
import com.flix.flix.model.response.StudioResponse;
import com.flix.flix.repository.StudioRepository;
import com.flix.flix.service.ProductPricingService;
import com.flix.flix.service.ProductSchedulingService;
import com.flix.flix.service.StudioService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudioServiceImpl implements StudioService {

    private final StudioRepository studioRepository;
    private final ProductPricingService productPricingService;
    private final ProductSchedulingService productSchedulingService;

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
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<StudioResponse> getAll() {
        try {
            return studioRepository.findAll().stream().map(this::toStudioResponse).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public StudioResponse getById(String id) {
        try {
            return toStudioResponse(getStudioById(id));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Studio getStudioById(String id) {
        try {
            Optional<Studio> studio = studioRepository.findById(id);
            if (studio.isEmpty()) throw new RuntimeException(DbBash.STUDIO_NOT_FOUND);
            return studio.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public StudioResponse update(String id, NewStudioRequest studioRequest) {
        try {
            List<String> availableListSeatRequest = studioRequest.getAvailableSeat();
            List<String> bookedListRequest = studioRequest.getBookedSeat();

            List<ESeat> availableSeatRequest = ESeat.toESeatList(availableListSeatRequest);
            List<ESeat> bookedSeatRequest = ESeat.toESeatList(bookedListRequest);

            Studio studio = getStudioById(id);
            studio.setName(studioRequest.getName());
            studio.setStudioSize(EStudioSize.findByDescription(studioRequest.getStudioSize()));
            List<ESeat> currentAvailableSeats = studio.getAvailableSeat();
            List<ESeat> currentBookedSeats = studio.getBookedSeat();

            validateSeatRequest(availableSeatRequest, bookedSeatRequest);

            List<ESeat> newBookedSeats = bookedSeatRequest.stream().filter(seat -> currentAvailableSeats.contains(seat)).toList();
            List<ESeat> newAvailableSeats = availableSeatRequest.stream().filter(seat -> currentBookedSeats.contains(seat)).toList();
            if (newBookedSeats.isEmpty() && newAvailableSeats.isEmpty() && !bookedSeatRequest.isEmpty()) {
                throw new RuntimeException(DbBash.SEAT_ALREADY_BOOKED);
            }

            availableSeatRequest.removeAll(currentBookedSeats);
            if (!availableSeatRequest.isEmpty()) availableSeatRequest.addAll(newAvailableSeats);

            studio.setAvailableSeat(availableSeatRequest);
            studio.setBookedSeat(bookedSeatRequest);

            // validasi product pricing request (dalam satu request tidak boleh ada dua product pricing berbeda dengan produt yang sama)
            Set<String> uniqueProductIdsInPricing = validateProductPricingRequest(studioRequest);

            // validasi product scheduling request (dalam satu request tidak boleh ada dua product schedulling berbeda dengan product yang sama)
            Set<String> uniqueProductIdsInScheduling = validateProductSchedulingRequest(studioRequest);

            // validasi product pricing and scheduling (tidak boleh ada product yang hanya memilki product pricing atau product scheduling saja)
            validateProductPricingAndScheduling(uniqueProductIdsInPricing, uniqueProductIdsInScheduling);

            // update product pricing
            List<ProductPricing> newProductPricings = getNewProductPricings(studioRequest, studio);
            studio.setProductPricing(newProductPricings);

            // update product scheduling
            List<ProductScheduling> newProductSchedulings = getNewProductSchedulings(studioRequest, studio);
            studio.setProductScheduling(newProductSchedulings);

            return toStudioResponse(studioRepository.saveAndFlush(studio));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(String id) {
        try {
            getStudioById(id);
            studioRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public StudioResponse toStudioResponse(Studio studio) {
        try {
            List<ProductPricingResponse> productPricings = new ArrayList<>();
            List<ProductSchedulingResponse> productSchedulings = new ArrayList<>();

            for (ProductPricing productPricing : studio.getProductPricing()) {
                productPricings.add(productPricingService.toProductPricingResponse(productPricing));
            }
            for (ProductScheduling productScheduling : studio.getProductScheduling()) {
                productSchedulings.add(productSchedulingService.toProductSchedulingResponse(productScheduling));
            }

            return StudioResponse.builder()
                    .id(studio.getId())
                    .name(studio.getName())
                    .studioSize(studio.getStudioSize())
                    .bookedSeat(studio.getBookedSeat())
                    .availableSeat(studio.getAvailableSeat())
                    .productPricing(productPricings)
                    .productScheduling(productSchedulings)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateSeatRequest(List<ESeat> availableSeatRequest, List<ESeat> bookedSeatRequest) {
        for (ESeat seat : availableSeatRequest) {
            if (bookedSeatRequest.contains(seat)) {
                throw new RuntimeException(DbBash.BOOKED_SEAT_AND_AVAILABLE_SEAT_NOT_MATCH);
            }
        }
        for (ESeat seat : bookedSeatRequest) {
            if (availableSeatRequest.contains(seat)) {
                throw new RuntimeException(DbBash.BOOKED_SEAT_AND_AVAILABLE_SEAT_NOT_MATCH);
            }
        }
    }

    private Set<String> validateProductPricingRequest(NewStudioRequest studioRequest) {
        Set<String> uniqueProductIds = new HashSet<>();
        List<String> allProductIds = studioRequest.getProductPricingRequests().stream()
            .map(productPricing -> productPricing.getProductId())
            .toList();
        for (String productId : allProductIds) {
            if (!uniqueProductIds.add(productId)) {
                throw new RuntimeException(DbBash.PRODUCT_ALREADY_PRICED);
            }
        }
        return uniqueProductIds;
    }

    private Set<String> validateProductSchedulingRequest(NewStudioRequest studioRequest) {
        Set<HashMap<String, String>> uniqueProductSchedules = new HashSet<>();
        Set<String> uniqueProductIds = new HashSet<>();

        List<HashMap<String, String>> allProductSchedule = studioRequest.getProductSchedulingRequests().stream()
            .map(productScheduling -> {
                HashMap<String, String> productSchedule = new HashMap<>();
                productSchedule.put(productScheduling.getProductId(), productScheduling.getSchedule());
                return productSchedule;
            }).toList();
        for (HashMap<String, String> productSchedule : allProductSchedule) {
            if (!uniqueProductSchedules.add(productSchedule)) {
                throw new RuntimeException(DbBash.PRODUCT_ALREADY_SCHEDULED);
            }
            uniqueProductIds.add(productSchedule.keySet().iterator().next());
        }

        return uniqueProductIds;
    }

    private void validateProductPricingAndScheduling(Set<String> uniqueProductIdsInPricing, Set<String> uniqueProductIdsInScheduling) {
        uniqueProductIdsInPricing.removeAll(uniqueProductIdsInScheduling);
        if (!uniqueProductIdsInPricing.isEmpty()) {
            throw new RuntimeException(DbBash.PRODUCT_PRICING_AND_PRODUCT_SCHEDULING_NOT_MATCH);
        }
    }

    private List<ProductPricing> getNewProductPricings(NewStudioRequest studioRequest, Studio studio) {
        for (ProductPricing productPricing : studio.getProductPricing()) {
            productPricing.getStudios().remove(studio);
        }
        List<ProductPricing> newProductPricings = new ArrayList<>();
        for (NewProductPricingRequest productPricingRequest : studioRequest.getProductPricingRequests()) {

            if (productPricingService.getProductPricingByAttribute(productPricingRequest) == null) {
                ProductPricing productPricing = productPricingService.create(productPricingRequest);
                newProductPricings.add(productPricing);
            } else {
                // ProductPricing productPricing = productPricingService.update(productPricingRequest.getId(), productPricingRequest);
                ProductPricing productPricing = productPricingService.getProductPricingByAttribute(productPricingRequest);
                newProductPricings.add(productPricing);
            }
        }
        for (ProductPricing productPricing : newProductPricings) {
            productPricing.getStudios().add(studio);
        }
        return newProductPricings;
    }

    private List<ProductScheduling> getNewProductSchedulings(NewStudioRequest studioRequest, Studio studio) {
        for (ProductScheduling productScheduling : studio.getProductScheduling()) {
            productScheduling.getStudios().remove(studio);
        }
        List<ProductScheduling> newProductSchedulings = new ArrayList<>();
        for (NewProductSchedulingRequest productSchedulingRequest : studioRequest.getProductSchedulingRequests()) {
            if (productSchedulingService.getProductSchedulingByAttribute(productSchedulingRequest) == null) {
                ProductScheduling productScheduling = productSchedulingService.create(productSchedulingRequest);
                newProductSchedulings.add(productScheduling);
            } else {
                // ProductScheduling productScheduling = productSchedulingService.update(productSchedulingRequest.getId(), productSchedulingRequest);
                ProductScheduling productScheduling = productSchedulingService.getProductSchedulingByAttribute(productSchedulingRequest);
                newProductSchedulings.add(productScheduling);
            }
        }
        for (ProductScheduling productScheduling : newProductSchedulings) {
            productScheduling.getStudios().add(studio);
        }
        return newProductSchedulings;
    }

}
