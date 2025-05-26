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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.STUDIO)
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    @PostMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<StudioResponse>> createStudio(
        @Valid @RequestBody NewStudioRequest studioRequest
    ) {
        CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
            .code(HttpStatus.CREATED.value())
            .message(ApiBash.CREATE_STUDIO_SUCCESS)
            .data(studioService.create(studioRequest))
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<List<StudioResponse>>> getAllStudio(
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

    @PutMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<StudioResponse>> updateStudio(
        @PathVariable String id, 
        @Valid @RequestBody NewStudioRequest newStudioRequest
    ) {
        CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.UPDATE_STUDIO_SUCCESS)
            .data(studioService.update(id, newStudioRequest))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<StudioResponse>> deleteStudio(
        @PathVariable String id
    ) {
        studioService.softDelete(id);
        CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.DELETE_STUDIO_SUCCESS)
            .data(null)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // cashier only //
    @GetMapping("/me")
    @PreAuthorize(ApiBash.HAS_ROLE_CASHIER)
    public ResponseEntity<CommonResponse<List<StudioResponse>>> getMyStudios(
        HttpServletRequest httpServletRequest
    ) {
        CommonResponse<List<StudioResponse>> response = CommonResponse.<List<StudioResponse>>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_STUDIO_SUCCESS)
            .data(studioService.getByCredentials(httpServletRequest).getContent())
            .paging(PagingUtils.pageToPagingResponse(studioService.getByCredentials(httpServletRequest)))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // cashier and customer only //
    @GetMapping("/{theaterId}/{productId}")
    @PreAuthorize(ApiBash.HAS_ROLE_CASHIER + " || " + ApiBash.HAS_ROLE_CUSTOMER)
    public ResponseEntity<CommonResponse<List<StudioResponse>>> getStudiosByProductIdAndTheaterId(
        @PathVariable String theaterId,
        @PathVariable String productId
    ) {
        CommonResponse<List<StudioResponse>> response = CommonResponse.<List<StudioResponse>>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_STUDIO_SUCCESS)
            .data(studioService.getByProductIdAndTheaterId(theaterId, productId).getContent())
            .paging(PagingUtils.pageToPagingResponse(studioService.getByProductIdAndTheaterId(theaterId, productId)))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // admin, cashier, customer only //
    @GetMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN + " || " + ApiBash.HAS_ROLE_CASHIER + " || " + ApiBash.HAS_ROLE_CUSTOMER)
    public ResponseEntity<CommonResponse<StudioResponse>> getStudioById(
        @PathVariable String id
    ) {
        CommonResponse<StudioResponse> response = CommonResponse.<StudioResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_STUDIO_SUCCESS)
            .data(studioService.getById(id))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
}
