package com.flix.flix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flix.flix.entity.ProductPricing;

@Repository
public interface ProductPricingRepository extends JpaRepository<ProductPricing, String> {

}
