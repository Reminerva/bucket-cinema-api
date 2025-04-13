package com.flix.flix.entity;

import java.util.ArrayList;
import java.util.List;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.customEnum.ESchedule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(name = DbBash.PRODUCT_SCHEDULING_DB)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductScheduling {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "schedule")
    @Enumerated(EnumType.STRING)
    private ESchedule schedule;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productIdScheduling;

    @OneToMany(mappedBy = "productScheduling")
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @ManyToMany
    @Builder.Default
    private List<ProductPricingScheduling> productPricingScheduling = new ArrayList<>();
}
