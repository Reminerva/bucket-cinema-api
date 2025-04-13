package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.MovieGenre;

public interface MovieGenreService {
    MovieGenre create(MovieGenre movieGenre);
    List<MovieGenre> getAll();
    MovieGenre getById(String id);
    MovieGenre update(MovieGenre movieGenre);
    void delete(String id);
}
