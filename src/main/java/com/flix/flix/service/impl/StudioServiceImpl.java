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
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductPricing;
import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.entity.Studio;
import com.flix.flix.entity.StudioSeatSchedule;
import com.flix.flix.model.request.NewProductPricingRequest;
import com.flix.flix.model.request.NewProductSchedulingRequest;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.request.NewStudioSeatScheduleRequest;
import com.flix.flix.model.response.ProductPricingResponse;
import com.flix.flix.model.response.ProductSchedulingResponse;
import com.flix.flix.model.response.StudioResponse;
import com.flix.flix.repository.StudioRepository;
import com.flix.flix.service.ProductPricingService;
import com.flix.flix.service.ProductSchedulingService;
import com.flix.flix.service.ProductService;
import com.flix.flix.service.StudioSeatScheduleService;
import com.flix.flix.service.StudioService;
import com.flix.flix.util.TimeUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudioServiceImpl implements StudioService {

    private final StudioRepository studioRepository;
    private final ProductPricingService productPricingService;
    private final ProductSchedulingService productSchedulingService;
    private final ProductService productService;
    private final StudioSeatScheduleService studioSeatScheduleService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public StudioResponse create(NewStudioRequest studioRequest) {
        try {
            List<String> seatLayoutRequest = studioRequest.getSeatLayout();

            List<ESeat> seatLayout = ESeat.toESeatList(seatLayoutRequest);
            Studio studio = Studio.builder()
                    .name(studioRequest.getName())    
                    .studioSize(EStudioSize.findByDescription(studioRequest.getStudioSize()))
                    .seatLayout(seatLayout)
                    .isActive(true)
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
    public List<StudioResponse> getAllActive() {
        try {
            return studioRepository.findAllByIsActive(true).stream().map(this::toStudioResponse).toList();
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

            Studio studio = getStudioById(id);
            studio.setName(studioRequest.getName());
            studio.setStudioSize(EStudioSize.findByDescription(studioRequest.getStudioSize()));
            // studio.setSeatLayout(ESeat.toESeatList(studioRequest.getSeatLayout()));

            // validasi product pricing request (dalam satu request tidak boleh ada dua product pricing berbeda dengan produt yang sama)
            Set<String> uniqueProductIdsInPricing = validateProductPricingRequest(studioRequest);

            // validasi product scheduling request (dalam satu request tidak boleh ada dua product scheduling berbeda dengan product yang sama)
            Set<String> uniqueProductIdsInScheduling = validateProductSchedulingRequest(studioRequest);

            // validasi product scheduling schedule (schedule tidak boleh tubrukan dengan durasi product)
            validateProductSchedulingRequestSchedule(studioRequest);

            // validasi product pricing and scheduling (tidak boleh ada product yang hanya memilki product pricing atau product scheduling saja)
            validateProductPricingAndScheduling(uniqueProductIdsInPricing, uniqueProductIdsInScheduling);

            // update product pricing
            List<ProductPricing> newProductPricings = getNewProductPricings(studioRequest, studio);
            studio.setProductPricing(newProductPricings);

            // update product scheduling
            List<ProductScheduling> newProductSchedulings = getNewProductSchedulings(studioRequest, studio);
            studio.setProductScheduling(newProductSchedulings);

            // update StudioSeatSchedule
            List<StudioSeatSchedule> newStudioSeatSchedules = new ArrayList<>();
            for (NewStudioSeatScheduleRequest studioSeatScheduleRequest : studioRequest.getStudioSeatScheduleRequests()) {
                if (studioSeatScheduleService.getStudioSeatScheduleByAttribute(studio.getId(), studioSeatScheduleRequest.getProductSchedulingId()) == null) {
                    studioSeatScheduleRequest.setAvailableSeat(ESeat.toESeatStringList(studio.getSeatLayout()));
                    studioSeatScheduleRequest.setBookedSeat(new ArrayList<>());
                    StudioSeatSchedule studioSeatSchedule = studioSeatScheduleService.create(studioSeatScheduleRequest);
                    newStudioSeatSchedules.add(studioSeatSchedule);
                } else {
                    StudioSeatSchedule studioSeatSchedule = studioSeatScheduleService.update(null, studioSeatScheduleRequest);
                    newStudioSeatSchedules.add(studioSeatSchedule);
                }
            }
            studio.setStudioSeatSchedule(newStudioSeatSchedules);

            return toStudioResponse(studioRepository.saveAndFlush(studio));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void softDelete(String id) {
        try {
            Studio studio = getStudioById(id);
            studio.setIsActive(false);
            studioRepository.saveAndFlush(studio);
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
                    .seatLayout(ESeat.toESeatStringList(studio.getSeatLayout()))
                    .studioSeatSchedule(studio.getStudioSeatSchedule().stream().map(studioSeatScheduleService::toStudioSeatScheduleResponse).toList())
                    .productPricing(productPricings)
                    .productScheduling(productSchedulings)
                    .isActive(studio.getIsActive())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
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

    private void validateProductSchedulingRequestSchedule(NewStudioRequest studioRequest) {
        Long minNextScheduleInLong = 0L;
        for (NewProductSchedulingRequest productSchedulingRequest : studioRequest.getProductSchedulingRequests()) {
            Product product = productService.getProductById(productSchedulingRequest.getProductId());
            Long productDuration = product.getDuration();
            String schedule = productSchedulingRequest.getSchedule();
            Long scheduleInLong = TimeUtil.stringToLongTimeMinutes(schedule);

            if (scheduleInLong <= minNextScheduleInLong) {
                throw new RuntimeException(DbBash.SCHEDULE_CONFLICT);
            }

            minNextScheduleInLong = scheduleInLong + productDuration;
        }
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
