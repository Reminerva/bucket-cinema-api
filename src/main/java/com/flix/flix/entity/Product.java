package com.flix.flix.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ECountry;
import com.flix.flix.constant.custom_enum.ELanguage;
import com.flix.flix.constant.custom_enum.ERated;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = DbBash.PRODUCT_DB)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "title")
    private String title;
    @Column
    private Long duration;
    @Column
    @Enumerated(EnumType.STRING)
    private ELanguage language;
    @Column
    @Enumerated(EnumType.STRING)
    private ECountry country;
    @Column(name = "release_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2023-03-15")
    private LocalDate releaseDate;
    @Column(name = "poster_url")
    private String posterUrl;
    @Column(name = "trailer_url")
    private String trailerUrl;
    @Column
    @Enumerated(EnumType.STRING)
    private ERated rated;
    @Column
    private Long budget;
    @Column
    private String synopsis;
    @Column
    private String tagline;
    @Column(name = "imdb_rating")
    @Max(10)
    @Min(0)
    private Double imdbRating;
    @Column(name = "rotten_tomatoes_rating")
    @Max(100)
    @Min(0)
    private Integer rottenTomatoesRating;
    @Column
    private String director;
    @Column
    private String writer;
    @Column
    private String producer;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MovieGenre> movieGenre = new ArrayList<>();

    @OneToMany(mappedBy = "productIdPricing", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductPricing> productPricing = new ArrayList<>();
    @OneToMany(mappedBy = "productIdScheduling", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductScheduling> productSchedule = new ArrayList<>();
    @OneToMany(mappedBy = "productId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductPricingScheduling> productPricingScheduling = new ArrayList<>();

    @Column(name = "last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2023-03-15")
    private LocalDate lastUpdated;
    @ManyToMany
    @JoinTable(
        name = "product_artist",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    @Builder.Default
    private List<Artist> artists = new ArrayList<>();
    @ManyToMany
    @Builder.Default
    private List<Customer> customerLike = new ArrayList<>();
    @ManyToMany
    @Builder.Default
    private List<Customer> customerDislike = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "production_company_id")
    private ProductionCompany productionCompany;

    @OneToMany(mappedBy = "product")
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    public Boolean containsArtist(Artist artist) {
        for (int i = 0; i < this.artists.size(); i++) {
            if (this.artists.get(i).equalsTo(artist)) {
                return true;
            }
        }
        return false;
    }

    public Boolean containsCustomerLike(Customer customer) {
        for (int i = 0; i < this.customerLike.size(); i++) {
            if (this.customerLike.get(i).equalsTo(customer)) {
                return true;
            }
        }
        return false;
    }

    public Boolean containsCustomerDislike(Customer customer) {
        for (int i = 0; i < this.customerDislike.size(); i++) {
            if (this.customerDislike.get(i).equalsTo(customer)) {
                return true;
            }
        }
        return false;
    }
}
