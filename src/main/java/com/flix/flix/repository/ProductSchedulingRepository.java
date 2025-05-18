package com.flix.flix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flix.flix.constant.custom_enum.ESchedule;
import com.flix.flix.entity.Product;
import com.flix.flix.entity.ProductScheduling;

@Repository
public interface ProductSchedulingRepository extends JpaRepository<ProductScheduling, String> {

    Optional<ProductScheduling> findProductSchedulingByScheduleAndProductIdScheduling(ESchedule schedule, Product product);

}
