package com.flix.flix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flix.flix.entity.MovieGenre;

@Repository
public interface MovieGenreRepository extends JpaRepository<MovieGenre, String> {

}
