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
                    .productIdScheduling(productService.getProductById(productSchedulingRequest.getProductId()))
                    .build();
            return productSchedulingRepository.saveAndFlush(productScheduling);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
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
    @Transactional(rollbackOn = Exception.class)
    public ProductScheduling update(String id, NewProductSchedulingRequest productSchedulingRequest) {
        try {
            ProductScheduling productScheduling = getProductSchedulingById(id);
            productScheduling.setSchedule(ESchedule.findByDescription(productSchedulingRequest.getSchedule()));
            return productSchedulingRepository.saveAndFlush(productScheduling);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(String id) {
        try {
            getProductSchedulingById(id);
            productSchedulingRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductSchedulingResponse toProductSchedulingResponse(ProductScheduling productScheduling) {
        return ProductSchedulingResponse.builder()
                .id(productScheduling.getId())
                .schedule(productScheduling.getSchedule().getDescription())
                .productId(productScheduling.getProductIdScheduling() == null ? null : productScheduling.getProductIdScheduling().getId())
                .build();
    }

    @Override
    public ProductScheduling getProductSchedulingByAttribute(NewProductSchedulingRequest productSchedulingRequest) {
        Optional<ProductScheduling> productScheduling = productSchedulingRepository.findProductSchedulingByScheduleAndProductIdScheduling(
            ESchedule.findByDescription(productSchedulingRequest.getSchedule()),
            productService.getProductById(productSchedulingRequest.getProductId())
        );
        if (productScheduling.isEmpty()) return null;
        return productScheduling.get();
    }
}
