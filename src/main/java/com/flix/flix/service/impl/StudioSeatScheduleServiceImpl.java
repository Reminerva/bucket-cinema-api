package com.flix.flix.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ESeat;
import com.flix.flix.entity.ProductScheduling;
import com.flix.flix.entity.Studio;
import com.flix.flix.entity.StudioSeatSchedule;
import com.flix.flix.model.request.NewStudioSeatScheduleRequest;
import com.flix.flix.model.response.StudioSeatScheduleResponse;
import com.flix.flix.repository.ProductSchedulingRepository;
import com.flix.flix.repository.StudioRepository;
import com.flix.flix.repository.StudioSeatScheduleRepository;
import com.flix.flix.service.StudioSeatScheduleService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudioSeatScheduleServiceImpl implements StudioSeatScheduleService {

    private final StudioSeatScheduleRepository studioSeatScheduleRepository;
    private final StudioRepository studioRepository;
    private final ProductSchedulingRepository productSchedulingRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public StudioSeatSchedule create(NewStudioSeatScheduleRequest studioSeatScheduleRequest, List<ESeat> seatLayout) {
        try {
            Optional<Studio> studioOptional = studioRepository.findById(studioSeatScheduleRequest.getStudioId());
            Optional<ProductScheduling> productSchedulingOptional = productSchedulingRepository.findById(studioSeatScheduleRequest.getProductSchedulingId());
            if (studioOptional.isEmpty()) throw new RuntimeException(DbBash.STUDIO_NOT_FOUND);
            if (productSchedulingOptional.isEmpty()) throw new RuntimeException(DbBash.PRODUCT_SCHEDULING_NOT_FOUND);
            validateSeatRequest(studioSeatScheduleRequest);
            validateSeatFromSeatLayout(studioSeatScheduleRequest, seatLayout);
            StudioSeatSchedule studioSeatSchedule = StudioSeatSchedule.builder()
                    .studio(studioOptional.get())
                    .productScheduling(productSchedulingOptional.get())
                    .availableSeat(ESeat.toESeatList(studioSeatScheduleRequest.getAvailableSeat()))
                    .bookedSeat(ESeat.toESeatList(studioSeatScheduleRequest.getBookedSeat()))
                    .build();
            return studioSeatScheduleRepository.saveAndFlush(studioSeatSchedule);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public StudioSeatSchedule getStudioSeatScheduleById(String id) {
        Optional<StudioSeatSchedule> studioSeatSchedule = studioSeatScheduleRepository.findById(id);
        if (studioSeatSchedule.isEmpty()) throw new RuntimeException(DbBash.STUDIO_SEAT_SCHEDULE_NOT_FOUND);
        return studioSeatSchedule.get();
    }

    @Override
    public List<StudioSeatSchedule> getAllStudioSeatSchedule() {
        try {
            return studioSeatScheduleRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public StudioSeatSchedule getStudioSeatScheduleByAttribute(String studioId, String productSchedulingId) {
        Optional<Studio> studioOptional = studioRepository.findById(studioId);
        Optional<ProductScheduling> productSchedulingOptional = productSchedulingRepository.findById(productSchedulingId);
        if (studioOptional.isEmpty()) throw new RuntimeException(DbBash.STUDIO_NOT_FOUND);
        if (productSchedulingOptional.isEmpty()) throw new RuntimeException(DbBash.PRODUCT_SCHEDULING_NOT_FOUND);
        Optional<StudioSeatSchedule> studioSeatScheduleOptional = studioSeatScheduleRepository.findByStudioAndProductScheduling(studioOptional.get(), productSchedulingOptional.get());
        if (studioSeatScheduleOptional.isEmpty()) return null;
        return studioSeatScheduleOptional.get();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public StudioSeatSchedule update(String id, NewStudioSeatScheduleRequest studioSeatScheduleRequest, List<ESeat> seatLayout) {
        try {
            validateSeatRequest(studioSeatScheduleRequest);
            validateSeatFromSeatLayout(studioSeatScheduleRequest, seatLayout);
            StudioSeatSchedule studioSeatSchedule = new StudioSeatSchedule();
            if (id != null) {
                studioSeatSchedule = getStudioSeatScheduleById(id);
            } else {
                studioSeatSchedule = getStudioSeatScheduleByAttribute(studioSeatScheduleRequest.getStudioId(), studioSeatScheduleRequest.getProductSchedulingId());
                if (studioSeatSchedule == null) {
                    throw new RuntimeException(DbBash.STUDIO_SEAT_SCHEDULE_NOT_FOUND);
                }
            }

            List<String> availableListSeatRequest = studioSeatScheduleRequest.getAvailableSeat();
            List<String> bookedListRequest = studioSeatScheduleRequest.getBookedSeat();

            List<ESeat> availableSeatRequest = ESeat.toESeatList(availableListSeatRequest);
            List<ESeat> bookedSeatRequest = ESeat.toESeatList(bookedListRequest);

            List<ESeat> currentAvailableSeats = studioSeatSchedule.getAvailableSeat();
            List<ESeat> currentBookedSeats = studioSeatSchedule.getBookedSeat();

            validateSeatConflictRequest(availableSeatRequest, bookedSeatRequest);

            List<ESeat> newBookedSeats = bookedSeatRequest.stream().filter(seat -> currentAvailableSeats.contains(seat)).toList();
            List<ESeat> newAvailableSeats = availableSeatRequest.stream().filter(seat -> currentBookedSeats.contains(seat)).toList();
            if (newBookedSeats.isEmpty() && newAvailableSeats.isEmpty() && !bookedSeatRequest.isEmpty()) {
                throw new RuntimeException(DbBash.SEAT_ALREADY_BOOKED);
            } 
            // else if (newBookedSeats.isEmpty() && newAvailableSeats.isEmpty() && !availableSeatRequest.isEmpty()) {
            //     throw new RuntimeException(DbBash.SEAT_ALREADY_AVAILABLE);
            // }

            availableSeatRequest.removeAll(currentBookedSeats);
            if (!availableSeatRequest.isEmpty()) availableSeatRequest.addAll(newAvailableSeats);

            studioSeatSchedule.setAvailableSeat(availableSeatRequest);
            studioSeatSchedule.setBookedSeat(bookedSeatRequest);
            
            return studioSeatScheduleRepository.saveAndFlush(studioSeatSchedule);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(String id) {
        try {
            getStudioSeatScheduleById(id);
            studioSeatScheduleRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public StudioSeatScheduleResponse toStudioSeatScheduleResponse(StudioSeatSchedule studioSeatSchedule) {
        try {
            return StudioSeatScheduleResponse.builder()
                    .id(studioSeatSchedule.getId())
                    .studioId(studioSeatSchedule.getStudio().getId())
                    .productSchedulingId(studioSeatSchedule.getProductScheduling().getId())
                    .availableSeat(ESeat.toESeatStringList(studioSeatSchedule.getAvailableSeat()))
                    .bookedSeat(ESeat.toESeatStringList(studioSeatSchedule.getBookedSeat()))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateSeatConflictRequest(List<ESeat> availableSeatRequest, List<ESeat> bookedSeatRequest) {
        for (ESeat seat : availableSeatRequest) {
            if (bookedSeatRequest.contains(seat)) {
                throw new RuntimeException(DbBash.BOOKED_SEAT_AND_AVAILABLE_SEAT_CONFLICT);
            }
        }
        // for (ESeat seat : bookedSeatRequest) {
        //     if (availableSeatRequest.contains(seat)) {
        //         throw new RuntimeException(DbBash.BOOKED_SEAT_AND_AVAILABLE_SEAT_CONFLICT);
        //     }
        // }
    }

    private void validateSeatRequest(NewStudioSeatScheduleRequest studioSeatScheduleRequest) {
        // cek duplikasi availableSeat
        if (studioSeatScheduleRequest.getAvailableSeat().size() != new HashSet<>(studioSeatScheduleRequest.getAvailableSeat()).size()) {
            throw new RuntimeException(DbBash.DUPLICATE_AVAILABLE_SEAT_REQUEST);
        }
        // cek duplikasi bookedSeat
        if (studioSeatScheduleRequest.getBookedSeat().size() != new HashSet<>(studioSeatScheduleRequest.getBookedSeat()).size()) {
            throw new RuntimeException(DbBash.DUPLICATE_BOOKED_SEAT_REQUEST);
        }
    }

    private void validateSeatFromSeatLayout(NewStudioSeatScheduleRequest studioSeatScheduleRequest, List<ESeat> seatLayout) {

        for (String seat : studioSeatScheduleRequest.getAvailableSeat()) {
            if (!seatLayout.contains(ESeat.findByDescription(seat))) {
                throw new RuntimeException(DbBash.AVAILABLE_SEAT_NOT_IN_SEAT_LAYOUT);
            }
        }
        for (String seat : studioSeatScheduleRequest.getBookedSeat()) {
            if (!seatLayout.contains(ESeat.findByDescription(seat))) {
                throw new RuntimeException(DbBash.BOOKED_SEAT_NOT_IN_SEAT_LAYOUT);
            }
        }
        for (ESeat seat : seatLayout) {
            if (!studioSeatScheduleRequest.getAvailableSeat().contains(seat.getDescription()) && !studioSeatScheduleRequest.getBookedSeat().contains(seat.getDescription())) {
                throw new RuntimeException(DbBash.SEAT_NOT_IN_REQUEST);
            }
        }
    }
}
