package com.flix.flix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.swagger_example.UserSwaggerExample;
import com.flix.flix.model.request.LoginRequest;
import com.flix.flix.model.request.NewUserRequest;
import com.flix.flix.model.request.search.SearchAppUserRequest;
import com.flix.flix.model.response.AppUserResponse;
import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.model.response.SigninResponse;
import com.flix.flix.model.response.SignoutResponse;
import com.flix.flix.model.response.SignupResponse;
import com.flix.flix.service.AppUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Auth API", description = "API untuk mengelola authentication (signup, signin & signout)")
@RequiredArgsConstructor
@RequestMapping(ApiBash.AUTH)
public class UserController {

    private final AppUserService userService;

    @Operation(
        summary = "Daftar akun", 
        description = "Melakukan signup dengan email, username dan password.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "User created successfully",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = UserSwaggerExample.SIGN_UP_SUCCESS)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = UserSwaggerExample.SIGN_UP_FAILED)
                )
            )
        }
    )
    @PostMapping(ApiBash.SIGN_UP)
    public ResponseEntity<CommonResponse<SignupResponse>> signup(
        @Schema(example = "{ \"email\": \"user@example.com\", \"password\": \"SecureUser123\" \"username\": \"user\" }")
        @RequestBody
        NewUserRequest userRequest,
        BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                String message = fieldError != null
                        ? fieldError.getDefaultMessage()
                        : bindingResult.getAllErrors().get(0).getDefaultMessage();
                    
                CommonResponse<SignupResponse> response = CommonResponse.<SignupResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message)
                    .data(null)
                    .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            CommonResponse<SignupResponse> response = CommonResponse.<SignupResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.SIGN_UP_SUCCESS)
                .data(userService.signup(userRequest))
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            String message = e.getMessage();
            if (e.getMessage().contains(DbBash.EMAIL_ALREADY_EXISTS_CONSTRAINT)) {message = (DbBash.EMAIL_ALREADY_EXISTS);};
            if (e.getMessage().contains(DbBash.USERNAME_ALREADY_EXISTS_CONSTRAINT)) {message = (DbBash.USERNAME_ALREADY_EXISTS);};
            CommonResponse<SignupResponse> response = CommonResponse.<SignupResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.SIGN_UP_FAILED + ": " + message)
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(
        summary = "Login akun", 
        description = "Melakukan signin dengan email dan password.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Product created successfully",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = UserSwaggerExample.SIGN_IN_SUCCESS)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = UserSwaggerExample.SIGN_IN_FAILED)
                )
            )
        }
    )

    @PostMapping(ApiBash.SIGN_IN)
    public ResponseEntity<CommonResponse<SigninResponse>> signin(
        @Schema(example = "{ \"email\": \"admin@example.com\", \"password\": \"SecureAdmin123\" }")
        @RequestBody
        LoginRequest loginRequest,
        BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                String message = fieldError != null
                        ? fieldError.getDefaultMessage()
                        : bindingResult.getAllErrors().get(0).getDefaultMessage();
                    
                CommonResponse<SigninResponse> response = CommonResponse.<SigninResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message)
                    .data(null)
                    .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            SigninResponse userAccount = userService.signin(loginRequest);
            CommonResponse<SigninResponse> response = CommonResponse.<SigninResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message(ApiBash.SIGN_IN_SUCCESS)
                .data(userAccount)
                .build();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            CommonResponse<SigninResponse> response = CommonResponse.<SigninResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.SIGN_IN_FAILED+ ": " + e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping(ApiBash.SIGN_OUT)
    public ResponseEntity<CommonResponse<SignoutResponse>> signout(HttpServletRequest signoutRequest) {
        try {
            SignoutResponse signoutResponse = userService.signout(signoutRequest);
            CommonResponse<SignoutResponse> response = CommonResponse.<SignoutResponse>builder()
                .code(HttpStatus.OK.value())
                .message(ApiBash.SIGN_OUT_SUCCESS)
                .data(signoutResponse)
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<SignoutResponse> response = CommonResponse.<SignoutResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.SIGN_OUT_FAILED+ ": " + e.getMessage())
                .data(null)
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
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
        try {
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
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CommonResponse<List<AppUserResponse>> response = CommonResponse.<List<AppUserResponse>>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(ApiBash.GET_ALL_USER_FAILED+ ": " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
