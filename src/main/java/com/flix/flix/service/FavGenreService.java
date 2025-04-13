package com.flix.flix.service;

import java.util.List;

import com.flix.flix.entity.FavGenre;

public interface FavGenreService {
    FavGenre create(FavGenre favGenre);
    List<FavGenre> getAll();
    FavGenre getFavGenreById(String id);
    FavGenre getById(String id);
    FavGenre update(String id, FavGenre favGenre);
    void delete(String id);
}
