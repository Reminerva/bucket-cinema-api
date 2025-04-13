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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.NewTheaterRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.TheaterResponse;
import com.flix.flix.service.TheaterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.THEATER)
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    @PostMapping
    public ResponseEntity<CommonResponse<TheaterResponse>> create(
        @Valid @RequestBody NewTheaterRequest newTheaterRequest,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(bindingResult.getFieldError().getDefaultMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_THEATER_SUCCESS)
                .data(theaterService.create(newTheaterRequest))
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<TheaterResponse>>> getAll() {
        try {
            CommonResponse<List<TheaterResponse>> response = CommonResponse.<List<TheaterResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_THEATER_SUCCESS)
                .data(theaterService.getAll())
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<List<TheaterResponse>> response = CommonResponse.<List<TheaterResponse>>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<CommonResponse<TheaterResponse>> getById(
        @PathVariable String id
    ) {
        try {
            CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_THEATER_SUCCESS)
                .data(theaterService.getById(id))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<CommonResponse<TheaterResponse>> update(
        @PathVariable String id, 
        @Valid @RequestBody NewTheaterRequest newTheaterRequest,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(bindingResult.getFieldError().getDefaultMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.UPDATE_THEATER_SUCCESS)
                .data(theaterService.update(id, newTheaterRequest))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<CommonResponse<TheaterResponse>> delete(@PathVariable String id) {
        try {
            theaterService.softDelete(id);
            CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.SOFT_DELETE_THEATER_SUCCESS)
                .data(theaterService.getById(id))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
