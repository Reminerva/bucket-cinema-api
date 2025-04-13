package com.flix.flix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flix.flix.entity.ProductScheduling;

@Repository
public interface ProductSchedulingRepository extends JpaRepository<ProductScheduling, String> {

}
