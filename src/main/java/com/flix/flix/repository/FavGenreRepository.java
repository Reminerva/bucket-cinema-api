package com.flix.flix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flix.flix.entity.FavGenre;

@Repository
public interface FavGenreRepository extends JpaRepository<FavGenre, String> {

}
