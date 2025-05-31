package com.flix.flix.util;

import com.flix.flix.constant.DbBash;
import com.flix.flix.entity.AppUser;
import com.flix.flix.repository.AppUserRepository;
import com.flix.flix.security.JwtAuthenticationFilter;
import com.flix.flix.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenUtilTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private AppUserRepository userAccountRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private TokenUtil tokenUtil;


    @Test
    void testGetAppUserByToken_validTokenAndUserFound_shouldReturnAppUser() {
        String testToken = "valid_testuser@example.com";
        String testUsername = "testuser@example.com";
        AppUser expectedUser = AppUser.builder().email(testUsername).build();

        when(jwtAuthenticationFilter.extractTokenFromRequest(httpServletRequest)).thenReturn(testToken);
        when(jwtTokenProvider.validateToken(testToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(testToken)).thenReturn(testUsername);
        when(userAccountRepository.findByEmail(testUsername)).thenReturn(Optional.of(expectedUser));

        AppUser actualUser = tokenUtil.getAppUserByToken(httpServletRequest);

        assertNotNull(actualUser, "AppUser should not be null");
        assertEquals(expectedUser.getEmail(), actualUser.getEmail(), "Returned user email should match expected");
        assertEquals(expectedUser.getId(), actualUser.getId(), "Returned user ID should match expected");

        verify(jwtAuthenticationFilter, times(1)).extractTokenFromRequest(httpServletRequest);
        verify(jwtTokenProvider, times(1)).validateToken(testToken);
        verify(jwtTokenProvider, times(1)).getUsernameFromToken(testToken);
        verify(userAccountRepository, times(1)).findByEmail(testUsername);
    }

    @Test
    void testGetAppUserByToken_nullToken_shouldThrowRuntimeException() {
        when(jwtAuthenticationFilter.extractTokenFromRequest(httpServletRequest)).thenReturn(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            tokenUtil.getAppUserByToken(httpServletRequest);
        }, "Should throw RuntimeException for null token");

        assertEquals("Invalid or Missing Token", thrown.getMessage(), "Exception message should match");

        verify(jwtAuthenticationFilter, times(1)).extractTokenFromRequest(httpServletRequest);
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(userAccountRepository);
    }

    @Test
    void testGetAppUserByToken_invalidToken_shouldThrowRuntimeException() {
        String invalidToken = "invalid_token";
        when(jwtAuthenticationFilter.extractTokenFromRequest(httpServletRequest)).thenReturn(invalidToken);
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            tokenUtil.getAppUserByToken(httpServletRequest);
        }, "Should throw RuntimeException for invalid token");

        assertEquals("Invalid or Missing Token", thrown.getMessage(), "Exception message should match");

        verify(jwtAuthenticationFilter, times(1)).extractTokenFromRequest(httpServletRequest);
        verify(jwtTokenProvider, times(1)).validateToken(invalidToken);
        verifyNoMoreInteractions(jwtTokenProvider);
        verifyNoInteractions(userAccountRepository);
    }

    @Test
    void testGetAppUserByToken_userNotFound_shouldThrowUsernameNotFoundException() {
        String testToken = "valid_nonexistent@example.com";
        String testUsername = "nonexistent@example.com";

        when(jwtAuthenticationFilter.extractTokenFromRequest(httpServletRequest)).thenReturn(testToken);
        when(jwtTokenProvider.validateToken(testToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(testToken)).thenReturn(testUsername);
        when(userAccountRepository.findByEmail(testUsername)).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            tokenUtil.getAppUserByToken(httpServletRequest);
        }, "Should throw UsernameNotFoundException if user is not found");

        assertEquals(DbBash.USER_NOT_FOUND, thrown.getMessage(), "Exception message should match constant");

        verify(jwtAuthenticationFilter, times(1)).extractTokenFromRequest(httpServletRequest);
        verify(jwtTokenProvider, times(1)).validateToken(testToken);
        verify(jwtTokenProvider, times(1)).getUsernameFromToken(testToken);
        verify(userAccountRepository, times(1)).findByEmail(testUsername);
    }

    @Test
    void testGetAppUserByToken_tokenExtractionReturnsNull_shouldThrowRuntimeException() {
        when(jwtAuthenticationFilter.extractTokenFromRequest(httpServletRequest)).thenReturn(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            tokenUtil.getAppUserByToken(httpServletRequest);
        }, "Should throw RuntimeException if token extraction returns null");

        assertEquals("Invalid or Missing Token", thrown.getMessage(), "Exception message should match");

        verify(jwtAuthenticationFilter, times(1)).extractTokenFromRequest(httpServletRequest);
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(userAccountRepository);
    }
}