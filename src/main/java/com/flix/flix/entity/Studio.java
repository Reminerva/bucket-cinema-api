package com.flix.flix.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.UniqueElements;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.customEnum.ESeat;
import com.flix.flix.constant.customEnum.EStudioSize;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = DbBash.STUDIO_DB)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Studio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String name;

    @Column(name = "studio_size")
    @Enumerated(EnumType.STRING)
    private EStudioSize studioSize;

    @Column(name = "booked_seat")
    @ElementCollection(targetClass = ESeat.class)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<ESeat> bookedSeat = new ArrayList<>();

    @Column(name = "available_seat")
    @ElementCollection(targetClass = ESeat.class)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<ESeat> availableSeat = new ArrayList<>();

    @ManyToMany
    @Builder.Default
    private List<ProductPricingScheduling> productPricingScheduling = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @OneToMany(mappedBy = "studio")
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
