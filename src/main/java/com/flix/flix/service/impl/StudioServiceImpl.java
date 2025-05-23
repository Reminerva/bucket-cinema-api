package com.flix.flix.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ESeat;
import com.flix.flix.constant.custom_enum.EStudioSize;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductPricing;
import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.entity.Studio;
import com.flix.flix.entity.StudioSeatSchedule;
import com.flix.flix.entity.Theater;
import com.flix.flix.model.request.NewProductPricingRequest;
import com.flix.flix.model.request.NewProductSchedulingRequest;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.request.NewStudioSeatScheduleRequest;
import com.flix.flix.model.request.search.SearchStudioRequest;
import com.flix.flix.model.response.ProductPricingResponse;
import com.flix.flix.model.response.ProductSchedulingResponse;
import com.flix.flix.model.response.StudioResponse;
import com.flix.flix.repository.StudioRepository;
import com.flix.flix.repository.TheaterRepository;
import com.flix.flix.service.ProductPricingService;
import com.flix.flix.service.ProductSchedulingService;
import com.flix.flix.service.ProductService;
import com.flix.flix.service.StudioSeatScheduleService;
import com.flix.flix.service.StudioService;
import com.flix.flix.specification.StudioSpecification;
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
    private final TheaterRepository theaterRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public StudioResponse create(NewStudioRequest studioRequest) {
        try {
            List<String> seatLayoutRequest = studioRequest.getSeatLayout();
            Optional<Theater> theater = theaterRepository.findById(studioRequest.getTheaterId());
            if (theater.isEmpty()) throw new RuntimeException(DbBash.THEATER_NOT_FOUND);

            List<ESeat> seatLayout = ESeat.toESeatList(seatLayoutRequest);
            if (!EStudioSize.isSeatValid(seatLayout, EStudioSize.findByDescription(studioRequest.getStudioSize()))) {
                throw new RuntimeException(DbBash.INVALID_SEAT_LAYOUT);
            }
            Studio studio = Studio.builder()
                    .name(studioRequest.getName())
                    .studioSize(EStudioSize.findByDescription(studioRequest.getStudioSize()))
                    .seatLayout(seatLayout)
                    .isActive(true)
                    .theater(theater.get())
                    .build();

            return toStudioResponse(studioRepository.saveAndFlush(studio));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<StudioResponse> getAll(SearchStudioRequest searchStudioRequest) {
        try {
            if (searchStudioRequest.getPage() <= 0) {
                searchStudioRequest.setPage(1);
            }
            if (searchStudioRequest.getSize() <= 0) {
                searchStudioRequest.setSize(10);
            }
            if (searchStudioRequest.getSeatLayout() != null && searchStudioRequest.getStudioSize() == null) {
                throw new RuntimeException(DbBash.INVALID_SEARCH_STUDIO_SEAT_LAYOUT_REQUEST);
            }
            Sort sort = Sort.by(Sort.Direction.fromString(searchStudioRequest.getDirection()), searchStudioRequest.getSortBy());
            Pageable pageable = PageRequest.of(searchStudioRequest.getPage() - 1, searchStudioRequest.getSize(), sort);
            Specification<Studio> specification = StudioSpecification.getSpecification(searchStudioRequest);
            return studioRepository.findAll(specification, pageable).map(this::toStudioResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<StudioResponse> getAllActive(SearchStudioRequest searchStudioRequest) {
        try {
            searchStudioRequest.setIsActive(true);
            return getAll(searchStudioRequest);
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
    public StudioResponse refreshAllSeat(String id) {
        try {
            if (LocalDateTime.now().getHour() >= 8 && LocalDateTime.now().getHour() <= 22) throw new RuntimeException(DbBash.SEAT_CAN_ONLY_REFRESHED_AFTER_22_BEFORE_8);
            Studio studio = getStudioById(id);
            studio.getStudioSeatSchedule().forEach(studioSeatSchedule -> {
                studioSeatSchedule.setAvailableSeat(ESeat.toESeatList(ESeat.toESeatStringList(studio.getSeatLayout())));
                studioSeatSchedule.setBookedSeat(new ArrayList<>());
            });
            return toStudioResponse(studioRepository.saveAndFlush(studio));
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
                    .studioSize(studio.getStudioSize().getDescription())
                    .seatLayout(ESeat.toESeatStringList(studio.getSeatLayout()))
                    .studioSeatSchedule(studio.getStudioSeatSchedule().stream().map(studioSeatScheduleService::toStudioSeatScheduleResponse).toList())
                    .productPricing(productPricings)
                    .productScheduling(productSchedulings)
                    .isActive(studio.getIsActive())
                    .theaterId(studio.getTheater() == null ? null : studio.getTheater().getId())
                    .theaterName(studio.getTheater() == null ? null : studio.getTheater().getName())
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
