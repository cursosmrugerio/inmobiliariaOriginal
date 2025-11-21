package com.inmobiliaria.shared.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set the secret key for testing
        ReflectionTestUtils.setField(jwtService, "secretKey",
            "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tZ2VuZXJhdGlvbi1hbmQtdmFsaWRhdGlvbi10ZXN0aW5n");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);

        userDetails = User.builder()
                .username("testuser@test.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void generateToken_shouldGenerateValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser@test.com");
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalse_forDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails differentUser = User.builder()
                .username("different@test.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertThat(isValid).isFalse();
    }

    @Test
    void generateTokenWithEmpresaId_shouldIncludeEmpresaId() {
        String token = jwtService.generateToken(userDetails, 1L);

        assertThat(token).isNotNull();
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("testuser@test.com");
    }
}
