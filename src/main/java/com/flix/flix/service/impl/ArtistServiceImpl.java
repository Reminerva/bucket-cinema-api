package com.flix.flix.service.impl;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flix.flix.constant.ApiBash;
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
            validateArtistRequest(artistRequest);
            Artist artist = Artist.builder()
                .name(artistRequest.getName())
                .placeOfBirth(artistRequest.getPlaceOfBirth())
                .birthDate(DateUtil.parseDate(artistRequest.getBirthDate()))
                .otherName(artistRequest.getOtherName())
                .bio(artistRequest.getBio())
                .artistTypes(EArtistType.toEArtistTypeList(artistRequest.getArtistTypes()))
                .build();

            return toArtistResponse(artistRepository.saveAndFlush(artist));
        } catch (Exception e) {
            throw new RuntimeException(ApiBash.CREATE_ARTIST_FAILED + ": " + e.getMessage());
        }
        
    }

    @Override
    public Page<ArtistResponse> getAll(SearchArtistRequest searchArtistRequest) {
        try {
            if (searchArtistRequest.getPage() <= 0) {
                searchArtistRequest.setPage(1);
            }
            if (searchArtistRequest.getSize() <= 0) {
                searchArtistRequest.setSize(10);
            }
            if (searchArtistRequest.getBirthDateMin() != null && searchArtistRequest.getBirthDateMax() != null) {
                if (DateUtil.parseDate(searchArtistRequest.getBirthDateMin()).isAfter(DateUtil.parseDate(searchArtistRequest.getBirthDateMax()))) {
                    throw new RuntimeException(DbBash.MIN_MAX_INVALID);
                }
            }
            Sort sort = Sort.by(Sort.Direction.fromString(searchArtistRequest.getDirection()), searchArtistRequest.getSortBy());
            Pageable pageable = PageRequest.of(searchArtistRequest.getPage() - 1, searchArtistRequest.getSize(), sort);
            Specification<Artist> specification = ArtistSpecification.getSpecification(searchArtistRequest);
            return artistRepository.findAll(specification, pageable).map(this::toArtistResponse);
        } catch (Exception e) {
            throw new RuntimeException(ApiBash.GET_ALL_ARTIST_FAILED + ": " + e.getMessage());
        }
    }

    @Override
    public ArtistResponse getById(String id) {
        try {
            return toArtistResponse(getArtistById(id));
        } catch (Exception e) {
            throw new RuntimeException(ApiBash.GET_ARTIST_FAILED + ": " + e.getMessage());
        }
    }

    @Override
    public Artist getArtistById(String id) {
        Optional<Artist> artist = artistRepository.findById(id);
        if (artist.isEmpty()) throw new RuntimeException(DbBash.ARTIST_NOT_FOUND);
        return artist.get();
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ArtistResponse update(String id, NewArtistRequest artistRequest) {
        try {
            validateArtistRequest(artistRequest);
            Artist artist = artistRepository.findById(id).orElseThrow(() -> new RuntimeException(DbBash.ARTIST_NOT_FOUND));
                artist.setName(artistRequest.getName());
                artist.setPlaceOfBirth(artistRequest.getPlaceOfBirth());
                artist.setBirthDate(DateUtil.parseDate(artistRequest.getBirthDate()));
                artist.setOtherName(artistRequest.getOtherName());
                artist.setBio(artistRequest.getBio());
                artist.setArtistTypes(EArtistType.toEArtistTypeList(artistRequest.getArtistTypes()));
            return toArtistResponse(artistRepository.saveAndFlush(artist));
        } catch (Exception e) {
            throw new RuntimeException(ApiBash.UPDATE_ARTIST_FAILED + ": " + e.getMessage());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void delete(String id) {
        try {
            artistRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(ApiBash.DELETE_ARTIST_FAILED + ": " + e.getMessage());
        }
    }

    private ArtistResponse toArtistResponse(Artist artist) {
        return ArtistResponse.builder()
                .id(artist.getId())
                .name(artist.getName())
                .placeOfBirth(artist.getPlaceOfBirth())
                .birthDate(artist.getBirthDate() == null ? null : artist.getBirthDate().toString())
                .otherName(artist.getOtherName())
                .bio(artist.getBio())
                .artistTypes(artist.getArtistTypes() == null ? null : EArtistType.toEArtistTypeStringList(artist.getArtistTypes()))
                .productTitle(artist.getInProduct() == null ? null : artist.getInProduct().stream().map(product -> product.getTitle()).toList())
                .build();
    }

    private void validateArtistRequest(NewArtistRequest artistRequest) {
        // cek duplikasi ArtistType
        if (artistRequest.getArtistTypes().size() != new HashSet<>(artistRequest.getArtistTypes()).size()) {
            throw new RuntimeException(DbBash.DUPLICATE_ARTIST_TYPE_REQUEST);
        }
    }
}
