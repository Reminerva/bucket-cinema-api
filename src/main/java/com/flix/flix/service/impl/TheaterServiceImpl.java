package com.flix.flix.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.entity.Studio;
import com.flix.flix.entity.Theater;
import com.flix.flix.model.request.NewTheaterRequest;
import com.flix.flix.model.response.TheaterResponse;
import com.flix.flix.repository.TheaterRepository;
import com.flix.flix.service.StudioService;
import com.flix.flix.service.TheaterService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TheaterServiceImpl implements TheaterService {
    
    private final TheaterRepository theaterRepository;
    private final StudioService studioService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TheaterResponse create(NewTheaterRequest theaterRequest) {
        try {
            Theater theater = Theater.builder()
                .name(theaterRequest.getName())
                .city(theaterRequest.getCity())
                .address(theaterRequest.getAddress())
                .contactNumber(theaterRequest.getContactNumber())
                .contactEmail(theaterRequest.getContactEmail())
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .oprationalStatus(true)
                .build();
    
            return toTheaterResponse(theaterRepository.saveAndFlush(theater));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TheaterResponse getById(String id) {
        try {
            return toTheaterResponse(getTheaterById(id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Theater getTheaterById(String id) {
        Optional<Theater> theater = theaterRepository.findById(id);
        if (theater.isEmpty()) throw new RuntimeException(DbBash.THEATER_NOT_FOUND);
        return theater.get();
    }

    @Override
    public List<TheaterResponse> getAll() {
        try {
            return theaterRepository.findAll().stream().map(this::toTheaterResponse).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TheaterResponse update(String id, NewTheaterRequest theaterRequest) {
        try {
            Theater theater = getTheaterById(id);
            theater.setName(theaterRequest.getName());
            theater.setCity(theaterRequest.getCity());
            theater.setAddress(theaterRequest.getAddress());
            theater.setContactNumber(theaterRequest.getContactNumber());
            theater.setContactEmail(theaterRequest.getContactEmail());
            theater.setUpdatedAt(LocalDate.now());
            theater.setOprationalStatus(theaterRequest.getOprationalStatus());

            List<Studio> studios = new ArrayList<>();
            for (String studioId : theaterRequest.getStudiosId()) {
                Studio studio = studioService.getStudioById(studioId);
                studio.setTheater(theater);
                studios.add(studio);
            }
            theater.setStudios(studios);
            return toTheaterResponse(theaterRepository.saveAndFlush(theater));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void softDelete(String id) {
        try {
            Theater theater = getTheaterById(id);
            theater.setOprationalStatus(false);
            theater.setUpdatedAt(LocalDate.now());
            theaterRepository.saveAndFlush(theater);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TheaterResponse toTheaterResponse(Theater theater) {
        return TheaterResponse.builder()
            .id(theater.getId())
            .name(theater.getName())
            .city(theater.getCity())
            .address(theater.getAddress())
            .contactNumber(theater.getContactNumber())
            .contactEmail(theater.getContactEmail())
            .createdAt(theater.getCreatedAt().toString())
            .updatedAt(theater.getUpdatedAt().toString())
            .oprationalStatus(theater.getOprationalStatus())
            .studios(theater.getStudios().stream().map(studioService::toStudioResponse).toList())
            .build();
    }
}
