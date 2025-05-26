package com.flix.flix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.NewArtistRequest;
import com.flix.flix.model.request.search.SearchArtistRequest;
import com.flix.flix.model.response.ArtistResponse;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.service.ArtistService;
import com.flix.flix.util.PagingUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiBash.ARTIST)
public class ArtistController {

    private final ArtistService artistService;

    // Admin only //
    @PostMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<ArtistResponse>> create (
        @Valid
        @RequestBody
        NewArtistRequest artistRequest
    ) {
        CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_ARTIST_SUCCESS)
                .data(artistService.create(artistRequest))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<List<ArtistResponse>>> getAll(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "name") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String placeOfBirth,
        @RequestParam(required = false) String birthDateMin,
        @RequestParam(required = false) String birthDateMax,
        @RequestParam(required = false) List<String> artistType,
        @RequestParam(required = false) List<String> inProductTitle

    ) {
        SearchArtistRequest searchArtistRequest = SearchArtistRequest.builder()
            .page(page)
            .size(size)
            .sortBy(sortBy)
            .direction(direction)
            .name(name)
            .placeOfBirth(placeOfBirth)
            .birthDateMin(birthDateMin)
            .birthDateMax(birthDateMax)
            .artistType(artistType)
            .inProductTitle(inProductTitle)
            .build();

        Page<ArtistResponse> artists = artistService.getAll(searchArtistRequest);
        CommonResponse<List<ArtistResponse>> response = CommonResponse.<List<ArtistResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_ARTIST_SUCCESS)
                .data(artists.getContent())
                .paging(PagingUtils.pageToPagingResponse(artists))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<ArtistResponse>> update(
        @PathVariable
        String id,
        @RequestBody
        @Valid
        NewArtistRequest artistRequest
    ) {
        CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.UPDATE_ARTIST_SUCCESS)
                .data(artistService.update(id, artistRequest))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<ArtistResponse>> delete (
        @PathVariable String id
    ) {
        artistService.delete(id);
        CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.DELETE_ARTIST_SUCCESS)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // admin and cashier //
    @GetMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN + " || " + ApiBash.HAS_ROLE_CASHIER)
    public ResponseEntity<CommonResponse<ArtistResponse>> getById(
        @PathVariable String id
    ) {
        CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_ARTIST_SUCCESS)
                .data(artistService.getById(id))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
