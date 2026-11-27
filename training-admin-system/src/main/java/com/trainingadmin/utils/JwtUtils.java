// 此文件已回滚至 Tlias 原版
package com.trainingadmin.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static final String SECRET_STRING =
            "aXRoZWltYV93ZWJfYWlfcHJvamVjdF9qd3Rfc2VjcmV0X2tleV8yMDI2";

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    private static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000L;

    private JwtUtils() {
    }

    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY)
                .compact();
    }

    public static Claims parseJWT(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
