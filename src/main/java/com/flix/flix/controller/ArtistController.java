package com.flix.flix.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.NewArtistRequest;
import com.flix.flix.model.response.ArtistResponse;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.service.ArtistService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiBash.ARTIST)
public class ArtistController {

    private final ArtistService artistService;

    @PostMapping
    public ResponseEntity<CommonResponse<ArtistResponse>> create (
        @Valid
        @RequestBody
        NewArtistRequest artistRequest,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(bindingResult.getFieldError().getDefaultMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                    .code(HttpStatus.CREATED.value())
                    .message(ApiBash.CREATE_ARTIST_SUCCESS)
                    .data(artistService.create(artistRequest))
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<ArtistResponse>>> getAll() {
        try {
            CommonResponse<List<ArtistResponse>> response = CommonResponse.<List<ArtistResponse>>builder()
                    .code(HttpStatus.OK.value())
                    .message(ApiBash.GET_ALL_ARTIST_SUCCESS)
                    .data(artistService.getAll())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<List<ArtistResponse>> response = CommonResponse.<List<ArtistResponse>>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())    
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ArtistResponse>> getById(
        @PathVariable String id
    ) {
        try {
            CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                    .code(HttpStatus.OK.value())
                    .message(ApiBash.GET_ARTIST_SUCCESS)
                    .data(artistService.getById(id))
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<ArtistResponse>> update(
        @PathVariable
        String id,
        @RequestBody
        NewArtistRequest artistRequest
    ) {
        try {
            CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                    .code(HttpStatus.OK.value())
                    .message(ApiBash.UPDATE_ARTIST_SUCCESS)
                    .data(artistService.update(id, artistRequest))
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<ArtistResponse>> delete (
        @PathVariable String id
    ) {
        try {
            ArtistResponse artistResponse = artistService.getById(id);
            artistService.delete(id);
            CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                    .code(HttpStatus.OK.value())
                    .message(ApiBash.DELETE_ARTIST_SUCCESS)
                    .data(artistResponse)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<ArtistResponse> response = CommonResponse.<ArtistResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
