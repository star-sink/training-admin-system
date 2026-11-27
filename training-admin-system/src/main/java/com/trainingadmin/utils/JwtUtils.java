package com.trainingadmin.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 *
 * <p>负责签发与解析令牌。密钥和有效期从配置项 {@code jwt.secret}、
 * {@code jwt.expiration} 读取，支持通过环境变量覆盖，避免把密钥硬编码在源码中。</p>
 */
@Component
public class JwtUtils {

    /** HMAC-SHA 密钥要求至少 32 字节（256 位） */
    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey key;
    private final long expirationMillis;

    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration:43200000}") long expirationMillis) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalArgumentException("jwt.secret 长度至少需要 32 字节");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;
    }

    /**
     * 生成 JWT 令牌
     *
     * @param claims 载荷数据，例如 id、username
     * @return JWT 令牌字符串
     */
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();
    }

    /**
     * 解析并校验 JWT 令牌
     *
     * @param token 纯 JWT 令牌字符串（不能带“Bearer ”等前缀）
     * @return 解析出的 Claims 载荷对象
     * @throws io.jsonwebtoken.JwtException 令牌非法或已过期时抛出
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
