package com.flix.flix.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    // Nilai untuk properti @Value
    private final Long TEST_EXPIRATION_TIME = TimeUnit.HOURS.toMillis(1); // 1 jam
    private final String TEST_SECRET_KEY = "iniAdalahSecretKeyYangSangatPanjangDanAmanUntukTestingDanHarusLebihDari256Bit";

    private String testUsername = "testuser@example.com";
    private String testRole = "ROLE_USER";
    private String testRole2 = "[ROLE_USER]";
    private String testRole3 = "[ROLE_USER";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "EXPIRATION_TIME", TEST_EXPIRATION_TIME);
        ReflectionTestUtils.setField(jwtTokenProvider, "SECRET_KEY", TEST_SECRET_KEY);
    }

    @Test
    void testGenerateToken() {
        String token = jwtTokenProvider.generateToken(testUsername, testRole);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(TEST_SECRET_KEY))
                                    .build()
                                    .verify(token);

        assertEquals(testUsername, decodedJWT.getSubject());
        assertEquals(testRole, decodedJWT.getClaim("role").asString());
        assertTrue(decodedJWT.getExpiresAt().after(new Date(System.currentTimeMillis() + TEST_EXPIRATION_TIME - 1000))); // Toleransi 1 detik
        assertTrue(decodedJWT.getExpiresAt().before(new Date(System.currentTimeMillis() + TEST_EXPIRATION_TIME + 1000))); // Toleransi 1 detik
        assertFalse(decodedJWT.getExpiresAt().after(new Date(System.currentTimeMillis() + TEST_EXPIRATION_TIME + 10000)));
    }

    @Test
    void testGetExpirationTime() {
        String token = jwtTokenProvider.generateToken(testUsername, testRole);
        long expirationTime = jwtTokenProvider.getExpirationTime(token);
        assertTrue(expirationTime > 0);
    }

    @Test
    void testGetRoleFromToken() {
        String token = jwtTokenProvider.generateToken(testUsername, testRole);
        String extractedRole = jwtTokenProvider.getRoleFromToken(token);
        assertEquals(testRole, extractedRole);
    
        String token2 = jwtTokenProvider.generateToken(testUsername, testRole2);
        String extractedRole2 = jwtTokenProvider.getRoleFromToken(token2);
        assertEquals(testRole, extractedRole2);
    
        String token3 = jwtTokenProvider.generateToken(testUsername, testRole3);
        String extractedRole3 = jwtTokenProvider.getRoleFromToken(token3);
        assertEquals(testRole3, extractedRole3);
    }

    @Test
    void testGetUsernameFromToken() {
        String token = jwtTokenProvider.generateToken(testUsername, testRole);
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void testValidateToken() throws InterruptedException {
        String token = jwtTokenProvider.generateToken(testUsername, testRole);
        assertTrue(jwtTokenProvider.validateToken(token));

        Long shortExpirationTime = 1L;
        ReflectionTestUtils.setField(jwtTokenProvider, "EXPIRATION_TIME", shortExpirationTime);

        String expiredToken = jwtTokenProvider.generateToken(testUsername, testRole);

        Thread.sleep(200);

        assertFalse(jwtTokenProvider.validateToken(expiredToken));

        ReflectionTestUtils.setField(jwtTokenProvider, "EXPIRATION_TIME", TEST_EXPIRATION_TIME);
    }
}
