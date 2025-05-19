package com.flix.flix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flix.flix.entity.Studio;

@Repository
public interface StudioRepository extends JpaRepository<Studio, String> {

    List<Studio> findAllByIsActive(boolean isActive);

}
