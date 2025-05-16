package com.flix.flix.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.EArtistType;
import com.flix.flix.entity.Artist;
import com.flix.flix.model.request.NewArtistRequest;
import com.flix.flix.model.request.search.SearchArtistRequest;
import com.flix.flix.model.response.ArtistResponse;
import com.flix.flix.repository.ArtistRepository;
import com.flix.flix.service.ArtistService;
import com.flix.flix.specification.ArtistSpecification;
import com.flix.flix.util.DateUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    
    private final ArtistRepository artistRepository;

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ArtistResponse create(NewArtistRequest artistRequest) {
        try {
            Artist artist = Artist.builder()
                .name(artistRequest.getName())
                .placeOfBirth(artistRequest.getPlaceOfBirth())
                .birthDate(DateUtil.parseDate(artistRequest.getBirthDate()))
                .otherName(artistRequest.getOtherName())
                .bio(artistRequest.getBio())
                .artistType(EArtistType.findByDescription(artistRequest.getArtistType()))
                .build();

            return toArtistResponse(artistRepository.saveAndFlush(artist));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        
    }

    @Override
    public Page<ArtistResponse> getAll(SearchArtistRequest searchArtistRequest) {
        try {
            if (searchArtistRequest.getPage() <= 0 || searchArtistRequest.getSize() <= 0) {
                searchArtistRequest.setPage(1);
                searchArtistRequest.setSize(10);
            }
            Sort sort = Sort.by(Sort.Direction.fromString(searchArtistRequest.getDirection()), searchArtistRequest.getSortBy());
            Pageable pageable = PageRequest.of(searchArtistRequest.getPage() - 1, searchArtistRequest.getSize(), sort);
            Specification<Artist> specification = ArtistSpecification.getSpecification(searchArtistRequest);
            return artistRepository.findAll(specification, pageable).map(this::toArtistResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ArtistResponse getById(String id) {
        Optional<Artist> artist = artistRepository.findById(id);
        if (artist.isEmpty()) throw new RuntimeException(DbBash.ARTIST_NOT_FOUND);
        return toArtistResponse(artist.get());
    }

    @Override
    public Artist findById(String id) {
        Optional<Artist> artist = artistRepository.findById(id);
        if (artist.isEmpty()) throw new RuntimeException(DbBash.ARTIST_NOT_FOUND);
        return artist.get();
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ArtistResponse update(String id, NewArtistRequest artistRequest) {
        try {
            Artist artist = artistRepository.findById(id).orElseThrow(() -> new RuntimeException(DbBash.ARTIST_NOT_FOUND));
                artist.setName(artistRequest.getName());
                artist.setPlaceOfBirth(artistRequest.getPlaceOfBirth());
                artist.setBirthDate(DateUtil.parseDate(artistRequest.getBirthDate()));
                artist.setOtherName(artistRequest.getOtherName());
                artist.setBio(artistRequest.getBio());
                artist.setArtistType(EArtistType.findByDescription(artistRequest.getArtistType()));
            return toArtistResponse(artistRepository.saveAndFlush(artist));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void delete(String id) {
        try {
            artistRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ArtistResponse toArtistResponse(Artist artist) {
        return ArtistResponse.builder()
                .id(artist.getId())
                .name(artist.getName())
                .placeOfBirth(artist.getPlaceOfBirth())
                .birthDate(artist.getBirthDate().toString())
                .otherName(artist.getOtherName())
                .bio(artist.getBio())
                .artistType(artist.getArtistType().getDescription())
                .productTitle(artist.getInProduct() == null ? null : artist.getInProduct().stream().map(product -> product.getTitle()).toList())
                .build();
    }
}
