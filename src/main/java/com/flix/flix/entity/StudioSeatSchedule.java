package com.flix.flix.entity;

import java.util.ArrayList;
import java.util.List;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ESeat;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = DbBash.STUDIO_SEAT_SCHEDULE_DB)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudioSeatSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "studio_id")
    private Studio studio;

    @ElementCollection(targetClass = ESeat.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = DbBash.STUDIO_SEAT_SCHEDULE_BOOKED_SEAT_DB,
        joinColumns = @jakarta.persistence.JoinColumn(name = "studio_seat_schedule_id")
    )
    @Column(name = "booked_seat")
    @Builder.Default
    private List<ESeat> bookedSeat = new ArrayList<>();

    @ElementCollection(targetClass = ESeat.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = DbBash.STUDIO_SEAT_SCHEDULE_AVAILABLE_SEAT_DB,
        joinColumns = @jakarta.persistence.JoinColumn(name = "studio_seat_schedule_id")
    )
    @Column(name = "available_seat")
    @Builder.Default
    private List<ESeat> availableSeat = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "product_scheduling_id")
    private ProductScheduling productScheduling;
}
