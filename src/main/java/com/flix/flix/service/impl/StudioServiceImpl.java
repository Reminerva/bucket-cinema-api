package com.flix.flix.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ESeat;
import com.flix.flix.constant.custom_enum.EStudioSize;
import com.flix.flix.entity.Studio;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.response.StudioResponse;
import com.flix.flix.repository.StudioRepository;
import com.flix.flix.service.StudioService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudioServiceImpl implements StudioService {

    private final StudioRepository studioRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public StudioResponse create(NewStudioRequest studioRequest) {
        try {
            List<String> availableSeatRequest = studioRequest.getAvailableSeat();

            List<ESeat> availableSeat = ESeat.toESeatList(availableSeatRequest);
            Studio studio = Studio.builder()
                    .name(studioRequest.getName())    
                    .studioSize(EStudioSize.findByDescription(studioRequest.getStudioSize()))
                    .availableSeat(availableSeat)
                    .build();

            return toStudioResponse(studioRepository.saveAndFlush(studio));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<StudioResponse> getAll() {
        try {
            return studioRepository.findAll().stream().map(this::toStudioResponse).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StudioResponse getById(String id) {
        try {
            return toStudioResponse(getStudioById(id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Studio getStudioById(String id) {
        try {
            Optional<Studio> studio = studioRepository.findById(id);
            if (studio.isEmpty()) throw new RuntimeException(DbBash.STUDIO_NOT_FOUND);
            return studio.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public StudioResponse update(String id, NewStudioRequest studioRequest) {
        try {
            List<String> availableListSeatRequest = studioRequest.getAvailableSeat();
            List<String> bookedListRequest = studioRequest.getBookedSeat();

            List<ESeat> availableSeatRequest = ESeat.toESeatList(availableListSeatRequest);
            List<ESeat> bookedSeatRequest = ESeat.toESeatList(bookedListRequest);

            Studio studio = getStudioById(id);
            studio.setName(studioRequest.getName());
            studio.setStudioSize(EStudioSize.findByDescription(studioRequest.getStudioSize()));
            List<ESeat> currentAvailableSeats = studio.getAvailableSeat();
            List<ESeat> currentBookedSeats = studio.getBookedSeat();

            for (ESeat seat : availableSeatRequest) {
                if (bookedSeatRequest.contains(seat)) {
                    throw new RuntimeException(DbBash.BOOKED_SEAT_AND_AVAILABLE_SEAT_NOT_MATCH);
                }
            }
            for (ESeat seat : bookedSeatRequest) {
                if (availableSeatRequest.contains(seat)) {
                    throw new RuntimeException(DbBash.BOOKED_SEAT_AND_AVAILABLE_SEAT_NOT_MATCH);
                }
            }

            List<ESeat> newBookedSeats = bookedSeatRequest.stream().filter(seat -> currentAvailableSeats.contains(seat)).toList();
            List<ESeat> newAvailableSeats = availableSeatRequest.stream().filter(seat -> currentBookedSeats.contains(seat)).toList();
            if (newBookedSeats.isEmpty() && newAvailableSeats.isEmpty() && !bookedSeatRequest.isEmpty()) {
                throw new RuntimeException(DbBash.SEAT_ALREADY_BOOKED);
            }

            availableSeatRequest.removeAll(currentBookedSeats);
            if (!availableSeatRequest.isEmpty()) availableSeatRequest.addAll(newAvailableSeats);

            studio.setAvailableSeat(availableSeatRequest);
            studio.setBookedSeat(bookedSeatRequest);


            return toStudioResponse(studioRepository.saveAndFlush(studio));
        } catch (Exception e) {
            // e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(String id) {
        try {
            getStudioById(id);
            studioRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StudioResponse toStudioResponse(Studio studio) {
        try {
            return StudioResponse.builder()
                    .id(studio.getId())
                    .name(studio.getName())
                    .studioSize(studio.getStudioSize())
                    .bookedSeat(studio.getBookedSeat())
                    .availableSeat(studio.getAvailableSeat())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
