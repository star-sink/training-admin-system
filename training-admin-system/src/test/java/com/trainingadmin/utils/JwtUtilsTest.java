// 此文件已回滚至 Tlias 原版
package com.trainingadmin.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtUtilsTest {

    @Test
    void shouldGenerateAndParseToken() {
        String token = JwtUtils.generateToken(Map.of("id", 1, "username", "zhangsan"));
        Claims claims = JwtUtils.parseJWT(token);

        assertEquals(1, claims.get("id", Integer.class));
        assertEquals("zhangsan", claims.get("username", String.class));
    }
}
