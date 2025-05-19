package com.flix.flix.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EGender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = DbBash.EMPLOYEE_DB)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String fullname;

    @Column(name = "nik_number", unique = true)
    private String nikNumber;

    @Column
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column
    @Enumerated(EnumType.STRING)
    private EGender gender;

    @Column
    private String city;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "date_of_appliment")
    private LocalDate dateOfAppliment;

    @OneToOne(mappedBy = "employee")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @OneToMany(mappedBy = "employee")
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

}
