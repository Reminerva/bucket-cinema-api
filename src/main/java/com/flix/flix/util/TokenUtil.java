package com.flix.flix.util;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.flix.flix.constant.DbBash;
import com.flix.flix.entity.AppUser;
import com.flix.flix.repository.AppUserRepository;
import com.flix.flix.security.JwtAuthenticationFilter;
import com.flix.flix.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TokenUtil {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AppUserRepository userAccountRepository;

    public AppUser getAppUserByToken(HttpServletRequest httpServletRequest) {
        String token = jwtAuthenticationFilter.extractTokenFromRequest(httpServletRequest);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Invalid or Missing Token");
        }
        String username = jwtTokenProvider.getUsernameFromToken(token);
        
        AppUser userAccount = userAccountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(DbBash.USER_NOT_FOUND));
        return userAccount;
    }
}
