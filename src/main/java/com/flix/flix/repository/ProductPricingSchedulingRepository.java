package com.flix.flix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flix.flix.entity.ProductPricingScheduling;

@Repository
public interface ProductPricingSchedulingRepository extends JpaRepository<ProductPricingScheduling, String> {

}
