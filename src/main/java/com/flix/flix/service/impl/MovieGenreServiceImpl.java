package com.flix.flix.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.entity.MovieGenre;
import com.flix.flix.repository.MovieGenreRepository;
import com.flix.flix.service.MovieGenreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieGenreServiceImpl implements MovieGenreService {

    private final MovieGenreRepository movieGenreRepository;

    @Override
    public MovieGenre create(MovieGenre movieGenre) {
        List<MovieGenre> movieGenres = getAll();
        for (MovieGenre genre : movieGenres) {
            if (genre.getProduct().getId() == (movieGenre.getProduct().getId()) && genre.getGenre() == (movieGenre.getGenre())) {
                return genre;
            };
        };
        return movieGenreRepository.saveAndFlush(movieGenre);
    }

    @Override
    public List<MovieGenre> getAll() {
        return movieGenreRepository.findAll();
    }

    @Override
    public MovieGenre getById(String id) {
        return movieGenreRepository.findById(id).orElseThrow(() -> new RuntimeException(DbBash.GENRE_NOT_FOUND));
    }

    @Override
    public MovieGenre update(MovieGenre movieGenre) {
        try {
            getById(movieGenre.getId());
            return movieGenreRepository.saveAndFlush(movieGenre);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try {
            getById(id);
            movieGenreRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
