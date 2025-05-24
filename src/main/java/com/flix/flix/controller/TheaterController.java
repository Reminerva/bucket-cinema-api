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
import com.flix.flix.model.request.NewTheaterRequest;
import com.flix.flix.model.request.search.SearchTheaterRequest;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.TheaterResponse;
import com.flix.flix.service.TheaterService;
import com.flix.flix.util.PagingUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.THEATER)
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    // admin only //
    @PostMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<TheaterResponse>> create(
        @Valid @RequestBody NewTheaterRequest newTheaterRequest
    ) {
        CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
            .code(HttpStatus.CREATED.value())
            .message(ApiBash.CREATE_THEATER_SUCCESS)
            .data(theaterService.create(newTheaterRequest))
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<TheaterResponse>> update(
        @PathVariable String id, 
        @Valid @RequestBody NewTheaterRequest newTheaterRequest
    ) {
        CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.UPDATE_THEATER_SUCCESS)
            .data(theaterService.update(id, newTheaterRequest))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<TheaterResponse>> delete(@PathVariable String id) {
        theaterService.softDelete(id);
        CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.SOFT_DELETE_THEATER_SUCCESS)
            .data(theaterService.getById(id))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}" + ApiBash.REFRESH_ALL_SEAT)
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN)
    public ResponseEntity<CommonResponse<TheaterResponse>> refreshAllSeat(
        @PathVariable String id
    ) {
        CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.REFRESH_ALL_SEAT_SUCCESS)
            .data(theaterService.refreshAllSeat(id))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // admin, cashier, customer only //
    @GetMapping
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN + " || " + ApiBash.HAS_ROLE_CASHIER + " || " + ApiBash.HAS_ROLE_CUSTOMER)
    public ResponseEntity<CommonResponse<List<TheaterResponse>>> getAll(
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "name") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String address,
        @RequestParam(required = false) String contactNumber,
        @RequestParam(required = false) String contactEmail,
        @RequestParam(required = false) String createdAtMin,
        @RequestParam(required = false) String createdAtMax,
        @RequestParam(required = false) String updatedAtMin,
        @RequestParam(required = false) String updatedAtMax,
        @RequestParam(required = false) Boolean oprationalStatus,
        @RequestParam(required = false) List<String> employeesName,
        @RequestParam(required = false) List<String> productsTitle,
        HttpServletRequest httpServletRequest
    ) {
        SearchTheaterRequest searchTheaterRequest = SearchTheaterRequest.builder()
            .page(page)
            .size(size)
            .sortBy(sortBy)
            .direction(direction)
            .name(name)
            .city(city) 
            .address(address)
            .contactNumber(contactNumber)
            .contactEmail(contactEmail)
            .createdAtMin(createdAtMin)
            .createdAtMax(createdAtMax)
            .updatedAtMin(updatedAtMin) 
            .updatedAtMax(updatedAtMax)
            .oprationalStatus(oprationalStatus)
            .employeesName(employeesName)
            .productsTitle(productsTitle)
            .build();
        Page<TheaterResponse> theaterResponses = theaterService.getAll(searchTheaterRequest, httpServletRequest);
        CommonResponse<List<TheaterResponse>> response = CommonResponse.<List<TheaterResponse>>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_ALL_THEATER_SUCCESS)
            .data(theaterResponses.getContent())
            .paging(PagingUtils.pageToPagingResponse(theaterResponses))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize(ApiBash.HAS_ROLE_ADMIN + " || " + ApiBash.HAS_ROLE_CASHIER + " || " + ApiBash.HAS_ROLE_CUSTOMER)
    public ResponseEntity<CommonResponse<TheaterResponse>> getById(
        @PathVariable String id
    ) {
        CommonResponse<TheaterResponse> response = CommonResponse.<TheaterResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.GET_THEATER_SUCCESS)
            .data(theaterService.getById(id))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
