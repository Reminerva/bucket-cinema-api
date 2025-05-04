package com.flix.flix.service.impl;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ERole;
import com.flix.flix.entity.AppUser;
import com.flix.flix.model.request.LoginRequest;
import com.flix.flix.model.request.NewUserRequest;
import com.flix.flix.model.response.SigninResponse;
import com.flix.flix.model.response.SignoutResponse;
import com.flix.flix.model.response.SignupResponse;
import com.flix.flix.repository.AppUserRepository;
import com.flix.flix.security.JwtAuthenticationFilter;
import com.flix.flix.security.JwtTokenProvider;
import com.flix.flix.service.AppUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenBlackListService redisTokenBlackListService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AppUser getAppUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException(DbBash.USER_NOT_FOUND));
    }

    @Override
    public SignupResponse signup(NewUserRequest userRequest) {
        try {
            List<ERole> roles = userRequest.getRole().stream().map(role -> ERole.findByDescription(role)).toList();
            AppUser user = AppUser.builder()
                .email(userRequest.getEmail())
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(roles)
                .build();

            userRepository.saveAndFlush(user);

            SignupResponse response = SignupResponse.builder()
                    .accountId(user.getId())
                    .email(user.getEmail())
                    .role(user.getRole().toString())
                    .build();

            return response;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public AppUser getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public SigninResponse signin(LoginRequest userRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword())
            );

            User user = (User) authentication.getPrincipal();

            String token = jwtTokenProvider.generateToken(user.getUsername(), user.getAuthorities().toString());

            SigninResponse response = SigninResponse.builder()
                .accountId(getByEmail(user.getUsername()).getId())
                .email(user.getUsername())
                .token(token)
                .role(user.getAuthorities().toString())
                .build();
    
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SignoutResponse signout(HttpServletRequest signoutRequest) {
        String token =  jwtAuthenticationFilter.extractTokenFromRequest(signoutRequest);
        
        if (token == null || !jwtTokenProvider.validateToken(token)) {

            throw new RuntimeException("Token is null");
        }

        Long expirationTime = jwtTokenProvider.getExpirationTime(token);
        redisTokenBlackListService.blackListToken(token, expirationTime);

        SignoutResponse response = SignoutResponse.builder()
            .statusMessage("Logout successful")
            .accessToken(token)
            .build();

        return response;
    }
}
