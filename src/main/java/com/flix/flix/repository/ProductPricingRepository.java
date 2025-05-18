package com.flix.flix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductPricing;

@Repository
public interface ProductPricingRepository extends JpaRepository<ProductPricing, String> {

    Optional<ProductPricing> findProductPricingByWeekdayPriceAndWeekendPriceAndIsPriceActiveAndProductIdPricing(Double weekdayPrice, Double weekendPrice, Boolean isPriceActive, Product product);

}
