package com.flix.flix.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.entity.ProductPricing;
import com.flix.flix.model.request.NewProductPricingRequest;
import com.flix.flix.model.response.ProductPricingResponse;
import com.flix.flix.repository.ProductPricingRepository;
import com.flix.flix.service.ProductPricingService;
import com.flix.flix.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductPricingServiceImpl implements ProductPricingService {
    
    private final ProductPricingRepository productPricingRepository;
    private final ProductService productService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductPricing create(NewProductPricingRequest productPricingRequest) {
        try {
            ProductPricing productPricing = ProductPricing.builder()
                    .weekdayPrice(productPricingRequest.getWeekdayPrice())
                    .weekendPrice(productPricingRequest.getWeekendPrice())
                    .weekdayPriceActive(true)
                    .weekendPriceActive(true)
                    .weekdayPriceDate(LocalDate.now())
                    .weekendPriceDate(LocalDate.now())
                    .productIdPricing(productService.getProductById(productPricingRequest.getProductIdPricing()))
                    .build();
            return productPricingRepository.saveAndFlush(productPricing);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProductPricing> getAll() {
        return productPricingRepository.findAll();
    }

    @Override
    public ProductPricing getProductPricingById(String id) {
        Optional<ProductPricing> productPricing = productPricingRepository.findById(id);
        if (productPricing.isEmpty()) throw new RuntimeException(DbBash.PRODUCT_PRICING_NOT_FOUND);
        return productPricing.get();
    }

    @Override
    public List<ProductPricing> getProductPricingByProductId(String id) {
        try {
            List<ProductPricing> productPricings = getAll();
            List<ProductPricing> productPricingsByProductId = productPricings.stream()
                .filter(productPricing -> 
                    productPricing.getProductIdPricing().getId().equals(id)
                ).toList();
            return productPricingsByProductId;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductPricing getProductPricingByPrice(List<ProductPricing> productPricings, Double weekdayPrice, Double weekendPrice) {
        try {
            List<ProductPricing> productPricingsByPrice = productPricings.stream()
                .filter(productPricing -> 
                    productPricing.getWeekdayPrice().equals(weekdayPrice) &&
                    productPricing.getWeekendPrice().equals(weekendPrice) &&
                    productPricing.getWeekdayPriceActive().equals(true) &&
                    productPricing.getWeekendPriceActive().equals(true)
                ).toList();
            if (productPricingsByPrice.isEmpty()) return null;
            return productPricingsByPrice.get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductPricing update(String id, NewProductPricingRequest productPricingRequest) {
        try {
            ProductPricing productPricing = getProductPricingById(id);
            productPricing.setWeekdayPrice(productPricingRequest.getWeekdayPrice());
            productPricing.setWeekendPrice(productPricingRequest.getWeekendPrice());
            productPricing.setWeekdayPriceActive(productPricingRequest.getWeekdayPriceActive());
            productPricing.setWeekendPriceActive(productPricingRequest.getWeekendPriceActive());
            return productPricingRepository.saveAndFlush(productPricing);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void softDelete(String id) {
        try {
            getProductPricingById(id);
            ProductPricing productPricing = productPricingRepository.findById(id).get();
            productPricing.setWeekdayPriceActive(false);
            productPricing.setWeekendPriceActive(false);
            
            productPricingRepository.saveAndFlush(productPricing);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductPricingResponse toProductPricingResponse(ProductPricing productPricing) {
        return ProductPricingResponse.builder()
                .id(productPricing.getId())
                .weekdayPrice(productPricing.getWeekdayPrice())
                .weekendPrice(productPricing.getWeekendPrice())
                .weekdayPriceActive(productPricing.getWeekdayPriceActive())
                .weekendPriceActive(productPricing.getWeekendPriceActive())
                .weekdayPriceDate(productPricing.getWeekdayPriceDate().toString())
                .weekendPriceDate(productPricing.getWeekendPriceDate().toString())
                .build();
    }
}
