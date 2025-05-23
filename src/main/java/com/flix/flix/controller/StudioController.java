package com.flix.flix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.NewStudioRequest;
import com.flix.flix.model.request.search.SearchSeatLayoutRequest;
import com.flix.flix.model.request.search.SearchStudioRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.StudioResponse;
import com.flix.flix.service.StudioService;
import com.flix.flix.util.PagingUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.STUDIO)
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    @PostMapping
    public ResponseEntity<CommonResponse<StudioResponse>> create(
        @Valid @RequestBody NewStudioRequest studioRequest
    ) {
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
                .message(ApiBash.CREATE_STUDIO_FAILED + ": " + e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<StudioResponse>>> getAll(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "name") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean isActive,
        @RequestParam(required = false) String studioSize,
        @RequestParam(required = false) String theaterName,
        @RequestParam(required = false) String theaterCity,
        @RequestBody(required = false) SearchSeatLayoutRequest seatLayout
    ) {
            SearchStudioRequest searchStudioRequest = SearchStudioRequest.builder()
                .name(name)
                .isActive(isActive)
                .studioSize(studioSize)
                .theaterName(theaterName)
                .theaterCity(theaterCity)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();
            
            if (seatLayout != null) searchStudioRequest.setSeatLayout(seatLayout.getSeatLayout());
            Page<StudioResponse> studios = studioService.getAll(searchStudioRequest);
            CommonResponse<List<StudioResponse>> response = CommonResponse.<List<StudioResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_STUDIO_SUCCESS)
                .data(studios.getContent())
                .paging(PagingUtils.pageToPagingResponse(studios))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        
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
                .message(ApiBash.GET_STUDIO_FAILED + ": " + e.getMessage())
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
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                    String message = fieldError != null
                        ? fieldError.getDefaultMessage()
                        : bindingResult.getAllErrors().get(0).getDefaultMessage();
    
                CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(ApiBash.UPDATE_STUDIO_FAILED + ": " + message)
                    .data(null)
                    .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.UPDATE_STUDIO_SUCCESS)
                .data(studioService.update(id, newStudioRequest))
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.UPDATE_STUDIO_FAILED + ": " + e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<StudioResponse>> delete(@PathVariable String id) {
        try {
            StudioResponse studioResponse = studioService.getById(id);
            studioService.softDelete(id);
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.DELETE_STUDIO_SUCCESS)
                .data(studioResponse)
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.DELETE_STUDIO_FAILED + ": " + e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
