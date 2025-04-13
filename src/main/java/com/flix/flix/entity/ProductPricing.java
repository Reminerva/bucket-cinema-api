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
    @Column(name = "weekday_price_date")
    private LocalDate weekdayPriceDate;
    @Column(name = "weekend_price_date")
    private LocalDate weekendPriceDate;
    @Column(name = "weekday_price_active")
    private Boolean weekdayPriceActive;
    @Column(name = "weekend_price_active")
    private Boolean weekendPriceActive;

    @JoinColumn(name = "product_id")
    @ManyToOne
    private Product productIdPricing;

    @OneToMany(mappedBy = "productPricing")
    @Builder.Default
    private List<ProductPricingScheduling> productPricingScheduling = new ArrayList<>();

    @OneToMany(mappedBy = "productPricing")
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
