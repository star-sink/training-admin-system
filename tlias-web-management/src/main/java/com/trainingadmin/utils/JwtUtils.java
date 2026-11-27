package com.trainingadmin.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    /**
     * 密钥：与原始测试类中保持一致
     */
    private static final String SECRET_STRING =
            "aXRoZWltYV93ZWJfYWlfcHJvamVjdF9qd3Rfc2VjcmV0X2tleV8yMDI2";

    /**
     * 将字符串密钥转换为 SecretKey
     */
    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    /**
     * 过期时间：12 小时
     */
    private static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000L;

    /**
     * 私有构造方法，避免工具类被实例化
     */
    private JwtUtils() {
    }

    /**
     * 生成 JWT 令牌
     *
     * @param claims 载荷数据，例如 id、username
     * @return JWT 令牌字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析 JWT 令牌
     *
     * @param token 纯 JWT 令牌字符串，不能带“生成的JWT令牌为:”等前缀
     * @return 解析出的 Claims 载荷对象
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}