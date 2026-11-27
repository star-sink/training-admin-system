package com.trainingadmin;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    /**
     * 生成JWT令牌 (适配 JJWT 0.12.6 + JDK 21)
     */
    @Test
    public void testGenerateJwt(){
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "1");
        dataMap.put("username", "admin");

        // 1. 准备密钥：新版强制要求 HS256 密钥长度必须 >= 32 字节 (256位)
        // 这里用一个很长的字符串来满足安全规范，不再使用教程里的短密钥
        String secretString = "aXRoZWltYV93ZWJfYWlfcHJvamVjdF9qd3Rfc2VjcmV0X2tleV8yMDI2";

        // 2. 将字符串转换为 SecretKey 对象
        SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes());

        // 3. 生成 JWT
        String jwt = Jwts.builder()
                .claims(dataMap) // 新版 API，替代老版的 addClaims
                .expiration(new Date(System.currentTimeMillis() + 3600 * 1000)) // 新版 API，替代老版的 setExpiration
                .signWith(key) // 直接传入 SecretKey，底层会自动识别使用 HS256 算法
                .compact();

        System.out.println("生成的JWT令牌为: " + jwt);
    }


    /**
     * 解析JWT令牌
     */
    @Test
    public void testParseJWT(){
        // 1. 注意：这里只放纯 Token 字符串，不能带“生成的JWT令牌为:”这个前缀
        String token = "eyJhbGciOiJIUzM4NCJ9.eyJpZCI6IjEiLCJ1c2VybmFtZSI6ImFkbWluIiwiZXhwIjoxNzkzNjM1NDAxfQ.vWv1soTFWj7p7Wq6Tu4tGSVjQFKnpsunT8oBLPHNtYMsRznxN8HtGqkVgjHWUi2u";

        // 2. 这里的密钥必须和生成时一模一样
        String secretString = "aXRoZWltYV93ZWJfYWlfcHJvamVjdF9qd3Rfc2VjcmV0X2tleV8yMDI2";
        SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes());

        // 3. 新版解析 API
        Claims claims = Jwts.parser()
                .verifyWith(key)            // 新版用 verifyWith() 验证签名密钥
                .build()                    // 构建解析器
                .parseSignedClaims(token)   // 解析带签名的 JWT (替代老版的 parseClaimsJws)
                .getPayload();              // 获取载荷信息 (替代老版的 getBody)

        // 4. 打印解析结果
        System.out.println("解析出的 Claims 对象: " + claims);
        System.out.println("提取用户名: " + claims.get("username"));
        System.out.println("提取ID: " + claims.get("id"));
    }
}