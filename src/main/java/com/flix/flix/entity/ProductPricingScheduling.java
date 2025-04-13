package com.flix.flix.entity;

import java.util.ArrayList;
import java.util.List;

import com.flix.flix.constant.DbBash;

import jakarta.persistence.Entity;
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
@Table(name = DbBash.PRODUCT_PRICING_SCHEDULING_DB)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPricingScheduling {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToMany(mappedBy = "productPricingScheduling")
    @Builder.Default
    private List<ProductScheduling> productScheduling = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "product_pricing_id")
    private ProductPricing productPricing;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @ManyToMany(mappedBy = "productPricingScheduling")
    @Builder.Default
    private List<Studio> studios = new ArrayList<>();
}
