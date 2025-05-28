package com.flix.flix.entity;

import java.util.ArrayList;
import java.util.List;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EArtistType;

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
@Table(name = DbBash.PRODUCT_ARTIST_DB)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ElementCollection(targetClass = EArtistType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = DbBash.PRODUCT_ARTIST_TYPE_DB,
        joinColumns = @jakarta.persistence.JoinColumn(name = "product_artist_id")
    )
    @Column(name = "artist_type")
    @Builder.Default
    private List<EArtistType> artistType = new ArrayList<>();
}
