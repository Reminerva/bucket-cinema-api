package com.flix.flix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.entity.Studio;
import com.flix.flix.entity.StudioSeatSchedule;

@Repository
public interface StudioSeatScheduleRepository extends JpaRepository<StudioSeatSchedule, String> {

    Optional<StudioSeatSchedule> findByStudioAndProductScheduling(Studio studio, ProductScheduling productScheduling);

}
