package com.flix.flix.entity;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EGenre;

import jakarta.persistence.Column;
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
@Table(name = DbBash.FAV_GENRE_DB)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "fav_genre")
    @Enumerated(EnumType.STRING)
    private EGenre favGenre;

    public Boolean equalsByName(String favGenreName) {
        return this.favGenre.toString().equalsIgnoreCase(favGenreName);
    }
}
