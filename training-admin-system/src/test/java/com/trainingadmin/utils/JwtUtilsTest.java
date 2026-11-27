package com.trainingadmin.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilsTest {

    private static final String SECRET = "training-admin-system-test-secret-key-2026";

    @Test
    void shouldGenerateAndParseToken() {
        JwtUtils jwtUtils = new JwtUtils(SECRET, 60_000);

        String token = jwtUtils.generateToken(Map.of("id", 1, "username", "zhangsan"));
        Claims claims = jwtUtils.parseToken(token);

        assertEquals(1, claims.get("id", Integer.class));
        assertEquals("zhangsan", claims.get("username", String.class));
    }

    @Test
    void shouldRejectExpiredToken() {
        JwtUtils jwtUtils = new JwtUtils(SECRET, -1_000);

        String expiredToken = jwtUtils.generateToken(Map.of("id", 1));

        assertThrows(ExpiredJwtException.class, () -> jwtUtils.parseToken(expiredToken));
    }

    @Test
    void shouldRejectShortSecret() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtils("too-short", 60_000));
    }
}
