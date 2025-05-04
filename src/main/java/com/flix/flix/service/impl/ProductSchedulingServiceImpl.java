package com.flix.flix.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ESchedule;
import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.model.request.NewProductSchedulingRequest;
import com.flix.flix.model.response.ProductSchedulingResponse;
import com.flix.flix.repository.ProductSchedulingRepository;
import com.flix.flix.service.ProductSchedulingService;
import com.flix.flix.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSchedulingServiceImpl implements ProductSchedulingService {

    private final ProductSchedulingRepository productSchedulingRepository;
    private final ProductService productService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductScheduling create(NewProductSchedulingRequest productSchedulingRequest) {
        try {
            ProductScheduling productScheduling = ProductScheduling.builder()
                    .schedule(ESchedule.findByDescription(productSchedulingRequest.getSchedule()))
                    .productIdScheduling(productService.getProductById(productSchedulingRequest.getProductIdScheduling()))
                    .build();
            return productSchedulingRepository.saveAndFlush(productScheduling);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProductScheduling> getAll() {
        return productSchedulingRepository.findAll();
    }

    @Override
    public ProductScheduling getProductSchedulingById(String id) {
        Optional<ProductScheduling> productScheduling = productSchedulingRepository.findById(id);
        if (productScheduling.isEmpty()) throw new RuntimeException(DbBash.PRODUCT_SCHEDULING_NOT_FOUND);
        return productScheduling.get();
    }

    @Override
    public List<ProductScheduling> getProductSchedulingByProductId(String id) {
        try {
            List<ProductScheduling> productSchedulings = getAll();
            return productSchedulings.stream().filter(productScheduling -> productScheduling.getProductIdScheduling().getId().equals(id)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProductScheduling> getProductSchedulingBySchedule(List<ProductScheduling> productSchedulings, String schedule) {
        try {
            return productSchedulings.stream().filter(productScheduling -> productScheduling.getSchedule().getDescription().equals(schedule)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProductScheduling> getProductSchedulingBySchedules(List<ProductScheduling> productSchedulings, List<String> schedules) {
        try {
            return productSchedulings.stream().filter(productScheduling -> schedules.contains(productScheduling.getSchedule().getDescription())).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductScheduling update(String id, NewProductSchedulingRequest productSchedulingRequest) {
        try {
            ProductScheduling productScheduling = getProductSchedulingById(id);
            productScheduling.setSchedule(ESchedule.findByDescription(productSchedulingRequest.getSchedule()));
            return productSchedulingRepository.saveAndFlush(productScheduling);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(String id) {
        try {
            getProductSchedulingById(id);
            productSchedulingRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductSchedulingResponse toProductSchedulingResponse(ProductScheduling productScheduling) {
        return ProductSchedulingResponse.builder()
                .id(productScheduling.getId())
                .schedule(productScheduling.getSchedule().getDescription())
                .build();
    }
}
