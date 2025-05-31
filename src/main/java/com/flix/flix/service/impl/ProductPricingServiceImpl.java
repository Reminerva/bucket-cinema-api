package com.flix.flix.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.entity.Product;
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
                    .isPriceActive(true)
                    .priceDate(LocalDate.now())
                    .productIdPricing(productService.getProductById(productPricingRequest.getProductId()))
                    .build();
            return productPricingRepository.saveAndFlush(productPricing);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
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
    @Transactional(rollbackOn = Exception.class)
    public ProductPricing update(String id, NewProductPricingRequest productPricingRequest) {
        try {
            ProductPricing productPricing = getProductPricingById(id);
            productPricing.setWeekdayPrice(productPricingRequest.getWeekdayPrice());
            productPricing.setWeekendPrice(productPricingRequest.getWeekendPrice());
            productPricing.setIsPriceActive(productPricingRequest.getIsPriceActive());
            return productPricingRepository.saveAndFlush(productPricing);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void softDelete(String id) {
        try {
            ProductPricing productPricing = getProductPricingById(id);
            productPricing.setIsPriceActive(false);
            productPricingRepository.saveAndFlush(productPricing);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductPricingResponse toProductPricingResponse(ProductPricing productPricing) {
        return ProductPricingResponse.builder()
                .id(productPricing.getId())
                .weekdayPrice(productPricing.getWeekdayPrice())
                .weekendPrice(productPricing.getWeekendPrice())
                .isPriceActive(productPricing.getIsPriceActive())
                .priceDate(productPricing.getPriceDate().toString())
                .productId(productPricing.getProductIdPricing() == null ? null : productPricing.getProductIdPricing().getId())
                .build();
    }

    @Override
    public ProductPricing getProductPricingByAttribute(NewProductPricingRequest productPricingRequest) {
        Double weekdayPrice = productPricingRequest.getWeekdayPrice();
        Double weekendPrice = productPricingRequest.getWeekendPrice();
        Boolean isPriceActive = productPricingRequest.getIsPriceActive();
        Product product = productService.getProductById(productPricingRequest.getProductId());
        Optional<ProductPricing> productPricing = productPricingRepository.findProductPricingByWeekdayPriceAndWeekendPriceAndIsPriceActiveAndProductIdPricing(weekdayPrice, weekendPrice, isPriceActive, product);
        if (productPricing.isEmpty()) return null;
        return productPricing.get();
    }
}
