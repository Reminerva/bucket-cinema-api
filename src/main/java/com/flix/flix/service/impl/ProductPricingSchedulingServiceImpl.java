package com.flix.flix.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductPricing;
import com.flix.flix.entity.ProductPricingScheduling;
import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.model.request.NewProductPricingRequest;
import com.flix.flix.model.request.NewProductPricingSchedulingRequest;
import com.flix.flix.model.request.NewProductSchedulingRequest;
import com.flix.flix.model.response.ProductPricingSchedulingResponse;
import com.flix.flix.model.response.ProductSchedulingResponse;
import com.flix.flix.repository.ProductPricingSchedulingRepository;
import com.flix.flix.service.ProductPricingSchedulingService;
import com.flix.flix.service.ProductPricingService;
import com.flix.flix.service.ProductSchedulingService;
import com.flix.flix.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductPricingSchedulingServiceImpl implements ProductPricingSchedulingService {

    private final ProductPricingSchedulingRepository productPricingSchedulingRepository;
    private final ProductService productService;
    private final ProductPricingService productPricingService;
    private final ProductSchedulingService productSchedulingService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductPricingSchedulingResponse create(NewProductPricingSchedulingRequest productPricingSchedulingRequest) {
        try {
            if (getProductPricingSchedulingByAttribute(productPricingSchedulingRequest) != null) {
                throw new RuntimeException(DbBash.PRODUCT_PRICING_SCHEDULING_ALREADY_EXISTS);
            }
            Product product = productService.getProductById(productPricingSchedulingRequest.getProductId());

            ProductPricing productPricings = getProductPricings(productPricingSchedulingRequest, product);

            List<ProductScheduling> productSchedulings = getProductSchedulings(productPricingSchedulingRequest, product);

            ProductPricingScheduling productPricingScheduling = ProductPricingScheduling.builder()
                .productId(product)
                .productPricing(productPricings)
                .productScheduling(productSchedulings)
                .build();

            for (ProductScheduling scheduling : productSchedulings) {
                scheduling.getProductPricingScheduling().add(productPricingScheduling);
            }
            return toProductPricingSchedulingResponse(productPricingSchedulingRepository.saveAndFlush(productPricingScheduling));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProductPricingSchedulingResponse> getAll() {
        try {
            List<ProductPricingScheduling> productPricingSchedulings = productPricingSchedulingRepository.findAll();
            return productPricingSchedulings.stream().map(productPricingScheduling -> toProductPricingSchedulingResponse(productPricingScheduling)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductPricingSchedulingResponse getById(String id) {
        try {
            return toProductPricingSchedulingResponse(getProductPricingSchedulingById(id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductPricingScheduling getProductPricingSchedulingById(String id) {
        Optional<ProductPricingScheduling> productPricingScheduling = productPricingSchedulingRepository.findById(id);
        if (productPricingScheduling.isEmpty()) throw new RuntimeException(DbBash.PRODUCT_PRICING_SCHEDULING_NOT_FOUND);
        return productPricingScheduling.get();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductPricingSchedulingResponse update(String id, NewProductPricingSchedulingRequest productPricingSchedulingRequest) {
        try {
            
            return null;
        } catch (Exception e) { 
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(String id) {
        try {
            getProductPricingSchedulingById(id);
            productPricingSchedulingRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductPricingScheduling getProductPricingSchedulingByAttribute(
            NewProductPricingSchedulingRequest productPricingSchedulingRequest) {
        try {
            Product product = productService.getProductById(productPricingSchedulingRequest.getProductId());
            ProductPricing productPricing = getProductPricings(productPricingSchedulingRequest, product);
            List<ProductScheduling> productSchedulings = getProductSchedulings(productPricingSchedulingRequest, product);
            
            List<ProductPricingSchedulingResponse> productPricingSchedulings = getAll();

            for (ProductPricingSchedulingResponse productPricingSchedulingResponse : productPricingSchedulings) {
                if (productPricingSchedulingResponse.getProductId().equals(product.getId())) {
                    if (productPricingSchedulingResponse.getProductPricing().getId().equals(productPricing.getId())) {
                        for (ProductSchedulingResponse productSchedulingResponse : productPricingSchedulingResponse.getProductScheduling()) {
                            if (productSchedulingResponse.getId().equals(productSchedulings.get(0).getId())) {
                                return getProductPricingSchedulingById(productPricingSchedulingResponse.getId());
                            }
                        }
                    }
                }
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductPricingSchedulingResponse toProductPricingSchedulingResponse(ProductPricingScheduling productPricingScheduling) {
        List<ProductSchedulingResponse> productSchedulingResponses = new ArrayList<>();
        List<ProductScheduling> productSchedulings = productPricingScheduling.getProductScheduling();
        productSchedulings.forEach(productScheduling -> 
            productSchedulingResponses.add(productSchedulingService.toProductSchedulingResponse(productScheduling))
        );
        return ProductPricingSchedulingResponse.builder()
                .id(productPricingScheduling.getId())
                .productId(productPricingScheduling.getProductId().getId())
                .productPricing(productPricingService.toProductPricingResponse(productPricingScheduling.getProductPricing()))
                .productScheduling(productSchedulingResponses)
                .build();
    }

    private ProductPricing getProductPricings(
            NewProductPricingSchedulingRequest productPricingSchedulingRequest, Product product
        ) {
        List<ProductPricing> productPricingsByProductId = productPricingService.getProductPricingByProductId(product.getId());
        if (productPricingsByProductId.isEmpty()) {
            NewProductPricingRequest newProductPricingRequest = NewProductPricingRequest.builder()
                .weekdayPrice(productPricingSchedulingRequest.getWeekdayPrice())
                .weekendPrice(productPricingSchedulingRequest.getWeekendPrice())
                .productIdPricing(product.getId())
                .build();
            
            ProductPricing productPricing = productPricingService.create(newProductPricingRequest);
            return productPricing;
        };

        ProductPricing productPricingsByPrice = productPricingService.getProductPricingByPrice(
            productPricingsByProductId, productPricingSchedulingRequest.getWeekdayPrice(), productPricingSchedulingRequest.getWeekendPrice()
        );
        if (productPricingsByPrice == null) {
            NewProductPricingRequest newProductPricingRequest = NewProductPricingRequest.builder()
                .weekdayPrice(productPricingSchedulingRequest.getWeekdayPrice())
                .weekendPrice(productPricingSchedulingRequest.getWeekendPrice())
                .productIdPricing(product.getId())
                .build();
            ProductPricing productPricing = productPricingService.create(newProductPricingRequest);
            
            productPricingsByPrice = productPricing;
        }
        return productPricingsByPrice;
    }

    private List<ProductScheduling> getProductSchedulings(NewProductPricingSchedulingRequest productPricingSchedulingRequest, Product product) {

        List<ProductScheduling> productSchedulingsByProductId = productSchedulingService.getProductSchedulingByProductId(product.getId());
        if (productSchedulingsByProductId.isEmpty()) {
            for (String schedule : productPricingSchedulingRequest.getSchedule()) {
                NewProductSchedulingRequest newProductSchedulingRequest = NewProductSchedulingRequest.builder()
                    .schedule(schedule)
                    .productIdScheduling(product.getId())
                    .build();
                productSchedulingService.create(newProductSchedulingRequest);
            }
            productSchedulingsByProductId = productSchedulingService.getProductSchedulingByProductId(product.getId());
        }
        
        for (String schedule : productPricingSchedulingRequest.getSchedule()) {
            List<ProductScheduling> productSchedulingsBySchedule =
                productSchedulingService.getProductSchedulingBySchedule(productSchedulingsByProductId, schedule);
            if (productSchedulingsBySchedule.isEmpty()) {
                NewProductSchedulingRequest newProductSchedulingRequest = NewProductSchedulingRequest.builder()
                    .schedule(schedule)
                    .productIdScheduling(product.getId())
                    .build();
                productSchedulingService.create(newProductSchedulingRequest);
            }
        }
        
        productSchedulingsByProductId = productSchedulingService.getProductSchedulingByProductId(product.getId());
        return productSchedulingService.getProductSchedulingBySchedules(productSchedulingsByProductId, productPricingSchedulingRequest.getSchedule());
        
    }
}
