package com.flix.flix.security;

import com.flix.flix.model.response.CommonResponse;
import com.flix.flix.service.impl.RedisTokenBlackListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private ObjectMapper objectMapper; 
    @Mock
    private RedisTokenBlackListService redisBlackListTokenService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String VALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImV4cCI6MTY3ODAyOTk4MiwiYXV0aCI6IlJPTEVfVVNFUiJ9.signature";
    private final String RAW_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImV4cCI6MTY3ODAyOTk4MiwiYXV0aCI6IlJPTEVfVVNFUiJ9.signature";
    private final String USERNAME = "testuser";
    private final String ROLE = "ROLE_USER";

    @BeforeEach
    void setUp() throws IOException {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testExtractTokenFromRequest_validBearerToken_shouldReturnRawToken() {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + RAW_TOKEN);
        String extractedToken = jwtAuthenticationFilter.extractTokenFromRequest(request);
        assertEquals(RAW_TOKEN, extractedToken);
    }

    @Test
    void testExtractTokenFromRequest_nullHeader_shouldReturnNull() {
        when(request.getHeader("Authorization")).thenReturn(null);
        String extractedToken = jwtAuthenticationFilter.extractTokenFromRequest(request);
        assertNull(extractedToken);
    }

    @Test
    void testExtractTokenFromRequest_emptyHeader_shouldReturnNull() {
        when(request.getHeader("Authorization")).thenReturn("");
        String extractedToken = jwtAuthenticationFilter.extractTokenFromRequest(request);
        assertNull(extractedToken);
    }

    @Test
    void testExtractTokenFromRequest_noBearerPrefix_shouldReturnNull() {
        when(request.getHeader("Authorization")).thenReturn("Token " + RAW_TOKEN);
        String extractedToken = jwtAuthenticationFilter.extractTokenFromRequest(request);
        assertNull(extractedToken);
    }

    @Test
    void testExtractTokenFromRequest_bearerPrefixOnly_shouldReturnEmptyString() {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");
        String extractedToken = jwtAuthenticationFilter.extractTokenFromRequest(request);
        assertEquals("", extractedToken);
    }



    @Test
    void testDoFilterInternal_nullToken_shouldContinueFilterChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null); 

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(tokenProvider); 
        verifyNoInteractions(redisBlackListTokenService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_blacklistedToken_shouldSetFailResponse() throws ServletException, IOException {
        when(objectMapper.writeValueAsString(any(CommonResponse.class))).thenReturn("{}", "{}");
        PrintWriter printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);

        when(request.getHeader("Authorization")).thenReturn(VALID_TOKEN);
        when(redisBlackListTokenService.isTokenBlacklisted(RAW_TOKEN)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(redisBlackListTokenService, times(1)).isTokenBlacklisted(RAW_TOKEN);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json");
        verify(objectMapper, times(1)).writeValueAsString(any(CommonResponse.class));
        verify(response.getWriter(), times(1)).write(anyString());
        verifyNoMoreInteractions(tokenProvider);
        verify(filterChain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_validToken_shouldAuthenticateAndContinueFilterChain() throws ServletException, IOException {

        when(request.getHeader("Authorization")).thenReturn(VALID_TOKEN);
        when(redisBlackListTokenService.isTokenBlacklisted(RAW_TOKEN)).thenReturn(false);
        when(tokenProvider.validateToken(RAW_TOKEN)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(RAW_TOKEN)).thenReturn(USERNAME);
        when(tokenProvider.getRoleFromToken(RAW_TOKEN)).thenReturn(ROLE);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(redisBlackListTokenService, times(1)).isTokenBlacklisted(RAW_TOKEN);
        verify(tokenProvider, times(1)).validateToken(RAW_TOKEN);
        verify(tokenProvider, times(1)).getUsernameFromToken(RAW_TOKEN);
        verify(tokenProvider, times(1)).getRoleFromToken(RAW_TOKEN);
        verify(filterChain, times(1)).doFilter(request, response);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertEquals(USERNAME, authentication.getPrincipal());
        assertEquals(1, authentication.getAuthorities().size());
        assertTrue(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ROLE)));
        assertTrue(authentication.getDetails() instanceof WebAuthenticationDetails);
    }

    @Test
    void testDoFilterInternal_invalidToken_shouldSetFailResponse() throws ServletException, IOException {
        when(objectMapper.writeValueAsString(any(CommonResponse.class))).thenReturn("{}", "{}");
        PrintWriter printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);

        when(request.getHeader("Authorization")).thenReturn(VALID_TOKEN);
        when(redisBlackListTokenService.isTokenBlacklisted(RAW_TOKEN)).thenReturn(false);
        when(tokenProvider.validateToken(RAW_TOKEN)).thenReturn(false); 

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(redisBlackListTokenService, times(1)).isTokenBlacklisted(RAW_TOKEN);
        verify(tokenProvider, times(1)).validateToken(RAW_TOKEN);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(objectMapper, times(1)).writeValueAsString(any(CommonResponse.class));
        verify(response.getWriter(), times(1)).write(anyString());
        verify(filterChain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_exceptionDuringTokenProcessing_shouldSetFailResponse() throws ServletException, IOException {
        when(objectMapper.writeValueAsString(any(CommonResponse.class))).thenReturn("{}", "{}");
        PrintWriter printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);

        when(request.getHeader("Authorization")).thenReturn(VALID_TOKEN);
        when(redisBlackListTokenService.isTokenBlacklisted(RAW_TOKEN)).thenReturn(false);
        doThrow(new RuntimeException("Test exception during token validation")).when(tokenProvider).validateToken(RAW_TOKEN);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(redisBlackListTokenService, times(1)).isTokenBlacklisted(RAW_TOKEN);
        verify(tokenProvider, times(1)).validateToken(RAW_TOKEN);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(objectMapper, times(1)).writeValueAsString(any(CommonResponse.class));
        verify(response.getWriter(), times(1)).write(anyString());
        verify(filterChain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_exceptionWhenWritingResponse_shouldHandleException() throws ServletException, IOException {

        when(request.getHeader("Authorization")).thenReturn(VALID_TOKEN);
        when(redisBlackListTokenService.isTokenBlacklisted(RAW_TOKEN)).thenReturn(true);
        doThrow(new IOException("Test IOException on write")).when(response).getWriter();

        assertThrows(IOException.class, () -> {
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        });

        verify(redisBlackListTokenService, times(1)).isTokenBlacklisted(RAW_TOKEN);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json");
        verify(objectMapper, times(1)).writeValueAsString(any(CommonResponse.class));
        verify(response, times(1)).getWriter();
        verify(filterChain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}