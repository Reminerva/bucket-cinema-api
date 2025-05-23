package com.flix.flix.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import com.flix.flix.model.request.search.SearchAppUserRequest;
import com.flix.flix.model.response.AppUserResponse;
import com.flix.flix.model.response.SigninResponse;
import com.flix.flix.model.response.SignoutResponse;
import com.flix.flix.model.response.SignupResponse;
import com.flix.flix.repository.AppUserRepository;
import com.flix.flix.security.JwtAuthenticationFilter;
import com.flix.flix.security.JwtTokenProvider;
import com.flix.flix.service.AppUserService;
import com.flix.flix.specification.AppUserSpecification;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenBlackListService redisTokenBlackListService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AppUser getAppUserById(String id) {
        return appUserRepository.findById(id).orElseThrow(() -> new RuntimeException(DbBash.USER_NOT_FOUND));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public SignupResponse signup(NewUserRequest userRequest) {
        try {
            List<ERole> roles = userRequest.getRole().stream().map(role -> ERole.findByDescription(role)).toList();
            AppUser user = AppUser.builder()
                .email(userRequest.getEmail())
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .roles(roles)
                .build();

            appUserRepository.saveAndFlush(user);

            SignupResponse response = SignupResponse.builder()
                    .accountId(user.getId())
                    .email(user.getEmail())
                    .role(user.getRoles().toString())
                    .build();

            return response;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public AppUser getByEmail(String email) {
        return appUserRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
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

    @Override
    public Page<AppUserResponse> getAll(SearchAppUserRequest searchAppUserRequest) {
        try {
            if (searchAppUserRequest.getPage() <= 0) {
                searchAppUserRequest.setPage(1);
            }
            if (searchAppUserRequest.getSize() <= 0) {
                searchAppUserRequest.setSize(10);
            }
            Sort sort = Sort.by(Sort.Direction.fromString(searchAppUserRequest.getDirection()), searchAppUserRequest.getSortBy());
            Pageable pageable = PageRequest.of(searchAppUserRequest.getPage() - 1, searchAppUserRequest.getSize(), sort);
            Specification<AppUser> specification = AppUserSpecification.getSpecification(searchAppUserRequest);
            return appUserRepository.findAll(specification, pageable).map(this::toAppUserResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private AppUserResponse toAppUserResponse(AppUser appUser) {
        try {
            return AppUserResponse.builder()
                .id(appUser.getId())
                .username(appUser.getUsername())
                .email(appUser.getEmail())
                .role(appUser.getRoles())
                .customerFullname(appUser.getCustomer() == null ? "" : appUser.getCustomer().getFullname())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
