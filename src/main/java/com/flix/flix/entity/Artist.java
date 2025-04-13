package com.flix.flix.entity;

import java.time.LocalDate;
import java.util.List;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.customEnum.EArtistType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = DbBash.ARTIST_DB)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String name;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "other_name")
    private String otherName;

    @Column
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "artist_type")
    private EArtistType artistType;

    @ManyToMany(mappedBy = "artists")
    private List<Product> inProduct;

    public Boolean equalsTo(Artist artist) {
        return this.id.equals(artist.getId());
    }
}
