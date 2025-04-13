package com.flix.flix.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.entity.FavGenre;
import com.flix.flix.repository.FavGenreRepository;
import com.flix.flix.service.FavGenreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavGenreServiceImpl implements FavGenreService {

    private final FavGenreRepository favGenreRepository;

    @Override
    public FavGenre create(FavGenre favGenre) {
        List<FavGenre> favGenres = getAll();
        for (FavGenre genre : favGenres) {
            if (genre.getCustomer().getId() == (favGenre.getCustomer().getId()) && genre.getFavGenre() == (favGenre.getFavGenre())) {
                return genre;
            };
        };
        return favGenreRepository.saveAndFlush(favGenre);
    }

    @Override
    public List<FavGenre> getAll() {
        return favGenreRepository.findAll();
    }

    @Override
    public FavGenre getFavGenreById(String id) {
        return favGenreRepository.findById(id).orElseThrow(() -> new RuntimeException(DbBash.GENRE_NOT_FOUND));
    }
    
    @Override
    public FavGenre getById(String id) {
        return favGenreRepository.findById(id).orElseThrow(() -> new RuntimeException(DbBash.GENRE_NOT_FOUND));
    }

    @Override
    public FavGenre update(String id, FavGenre favGenre) {
        try {
            getById(id);
            return favGenreRepository.saveAndFlush(favGenre);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try {
            getById(id);
            favGenreRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
