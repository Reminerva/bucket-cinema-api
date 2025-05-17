package com.flix.flix.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.flix.flix.constant.DbBash;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = DbBash.PRODUCT_PRICING_DB)
public class ProductPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "weekday_price")
    private Double weekdayPrice;
    @Column(name = "weekend_price")
    private Double weekendPrice;
    @Column(name = "price_date")
    private LocalDate priceDate;
    @Column(name = "is_price_active")
    private Boolean isPriceActive;

    @JoinColumn(name = "product_id")
    @ManyToOne
    private Product productIdPricing;

    @ManyToMany
    @JoinTable(
        name = "t_product_pricing_studios", // Nama tabel penghubung yang Anda inginkan
        joinColumns = @JoinColumn(name = "product_pricing_id"), // Kolom untuk foreign key ke tabel ProductPricing
        inverseJoinColumns = @JoinColumn(name = "studio_id") // Kolom untuk foreign key ke tabel Studio
    )
    @Builder.Default
    private List<Studio> studios = new ArrayList<>();

    @OneToMany(mappedBy = "productPricing")
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
