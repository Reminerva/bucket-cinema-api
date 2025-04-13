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
import com.flix.flix.entity.Studio;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.StudioResponse;
import com.flix.flix.service.StudioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.STUDIO)
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    @PostMapping
    public ResponseEntity<CommonResponse<StudioResponse>> create(
        @Valid @RequestBody NewStudioRequest studioRequest,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(bindingResult.getFieldError().getDefaultMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_STUDIO_SUCCESS)
                .data(studioService.create(studioRequest))
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<StudioResponse>>> getAll() {
        try {
            CommonResponse<List<StudioResponse>> response = CommonResponse.<List<StudioResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_STUDIO_SUCCESS)
                .data(studioService.getAll())
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<List<StudioResponse>> response = CommonResponse.<List<StudioResponse>>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<StudioResponse>> getById(
        @PathVariable String id
    ) {
        try {
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_STUDIO_SUCCESS)
                .data(studioService.getById(id))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<StudioResponse>> update(
        @PathVariable String id, 
        @Valid @RequestBody NewStudioRequest newStudioRequest,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(bindingResult.getFieldError().getDefaultMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.UPDATE_STUDIO_SUCCESS)
                .data(studioService.update(id, newStudioRequest))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<StudioResponse>> delete(@PathVariable String id) {
        try {
            StudioResponse studioResponse = studioService.getById(id);
            studioService.delete(id);
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.DELETE_STUDIO_SUCCESS)
                .data(studioResponse)
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
