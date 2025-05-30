package com.flix.flix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.model.request.LoginRequest;
import com.flix.flix.model.request.NewCustomerRequest;
import com.flix.flix.model.request.search.SearchAppUserRequest;
import com.flix.flix.model.response.AppUserResponse;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.CustomerResponse;
import com.flix.flix.model.response.SigninResponse;
import com.flix.flix.model.response.SignoutResponse;
import com.flix.flix.service.AppUserService;
import com.flix.flix.service.CustomerService;
import com.flix.flix.util.PagingUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping(ApiBash.USER)
public class UserController {

    private final AppUserService userService;
    private final CustomerService customerService;

    @PostMapping(ApiBash.AUTH + ApiBash.SIGN_UP)
    public ResponseEntity<CommonResponse<CustomerResponse>> signup(
        @Valid
        @RequestBody
        NewCustomerRequest newCustomerRequest
    ) {
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
            .code(HttpStatus.CREATED.value())
            .message(ApiBash.SIGN_UP_SUCCESS)
            .data(customerService.create(newCustomerRequest))
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(ApiBash.AUTH + ApiBash.SIGN_IN)
    public ResponseEntity<CommonResponse<SigninResponse>> signin(
        @Valid
        @RequestBody
        LoginRequest loginRequest
    ) {
        SigninResponse userAccount = userService.signin(loginRequest);
        CommonResponse<SigninResponse> response = CommonResponse.<SigninResponse>builder()
            .code(HttpStatus.ACCEPTED.value())
            .message(ApiBash.SIGN_IN_SUCCESS)
            .data(userAccount)
            .build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(ApiBash.AUTH + ApiBash.SIGN_OUT)
    public ResponseEntity<CommonResponse<SignoutResponse>> signout(HttpServletRequest signoutRequest) {
        SignoutResponse signoutResponse = userService.signout(signoutRequest);
        CommonResponse<SignoutResponse> response = CommonResponse.<SignoutResponse>builder()
            .code(HttpStatus.OK.value())
            .message(ApiBash.SIGN_OUT_SUCCESS)
            .data(signoutResponse)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<AppUserResponse>>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "username") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String customerFullname,
            @RequestParam(required = false) List<String> role
    ) {
        SearchAppUserRequest searchAppUserRequest = SearchAppUserRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .username(username)
                .email(email)
                .customerFullname(customerFullname)
                .role(role)
                .build();
        Page<AppUserResponse> appUsers = userService.getAll(searchAppUserRequest);
        CommonResponse<List<AppUserResponse>> response = CommonResponse.<List<AppUserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_USER_SUCCESS)
                .data(appUsers.getContent())
                .paging(PagingUtils.pageToPagingResponse(appUsers))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
