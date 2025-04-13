package com.flix.flix.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.customEnum.ECountry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = DbBash.PRODUCTION_COMPANY_DB)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "origin_country")
    @Enumerated(EnumType.STRING)
    private ECountry originCountry;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "founded_year")
    private LocalDate foundedYear;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "headquarters")
    private String headquarters;

    @Column(name = "ceo")
    private String ceo;

    @Column
    private String description;
    
    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "productionCompany")
    @Builder.Default
    private List<Product> hasProduct = new ArrayList<>();
}
