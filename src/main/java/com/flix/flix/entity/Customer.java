package com.flix.flix.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EGender;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = DbBash.CUSTOMER_DB)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String fullname;

    @Column
    private String country;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column
    private String city;

    @Column
    @Enumerated(EnumType.STRING)
    private EGender gender;

    @Column(name = "registration_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2025-04-05")
    private LocalDate registrationDate;

    @Column(name = "last_login")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2025-04-05")
    private LocalDate lastLogin;

    @OneToMany(mappedBy="customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FavGenre> favGenre = new ArrayList<>();

    @OneToOne(mappedBy = "customer")
    private AppUser appUser;

    @ManyToMany(mappedBy = "customerLike", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Builder.Default
    private List<Product> likeProduct = new ArrayList<>();
    @ManyToMany(mappedBy = "customerDislike", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Builder.Default
    private List<Product> dislikeProduct = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    @Builder.Default
    private List<Transaction> transactions= new ArrayList<>();

    public Boolean equalsTo(Customer customer) {
        return this.id.equals(customer.getId());
    }

    public Boolean containingFavGenre(String favGenre) {
        for (FavGenre fg : this.favGenre) {
            if (fg.equalsByName(favGenre)) {
                return true;
            }
        }
        return false;
    }
}
