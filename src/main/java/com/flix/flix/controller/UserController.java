package com.flix.flix.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flix.flix.constant.ApiBash;
import com.flix.flix.constant.swagger_example.UserSwaggerExample;
import com.flix.flix.model.request.LoginRequest;
import com.flix.flix.model.request.NewAdminRequest;
import com.flix.flix.model.request.NewUserRequest;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Auth API", description = "API untuk mengelola authentication (signup, signin & signout)")
@RequiredArgsConstructor
@RequestMapping(ApiBash.AUTH)
public class UserController {

    private final AppUserService userService;

    @Operation(
        summary = "Registrasi akun", 
        description = "Membuat akun baru.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Product created successfully",
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
    @PostMapping(ApiBash.ADMIN_SIGN_UP)
    public ResponseEntity<CommonResponse<SignupResponse>> signupAdmin(
            @Valid 
            @RequestBody
            @Schema(example = "{ \"email\": \"admin@example.com\", \"password\": \"SecureAdmin123\" }")
            NewAdminRequest request,
            BindingResult bindingResult
    ) {
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
        try {
            NewUserRequest userRequest = NewUserRequest.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .role(List.of("ADMIN"))
                    .build();

            SignupResponse newUser = userService.signup(userRequest);
            CommonResponse<SignupResponse> response = CommonResponse.<SignupResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(ApiBash.SIGN_UP_SUCCESS)
                .data(newUser)
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            CommonResponse<SignupResponse> response = CommonResponse.<SignupResponse>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ApiBash.SIGN_UP_FAILED+ ": " + e.getMessage())
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
    
}
