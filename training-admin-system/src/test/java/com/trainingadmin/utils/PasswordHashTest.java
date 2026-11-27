package com.trainingadmin.utils;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHashTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 校验 training.sql 中内置的演示账号密码哈希均对应明文 123456
     */
    @Test
    void seededHashesShouldMatchDemoPassword() {
        List<String> hashes = List.of(
                "$2a$10$VANKPqW/j.tshIwQHomOwOlSG2o812gbG44IewyKNSF0bObI3bzZW",
                "$2a$10$Twa5PP2421oCOOkFqI7a2OX/ZUPK5JoU.4z7DS0FsDemDYvavI1ei",
                "$2a$10$jT75/lE7oPQ6JHjCvz9c0OhQmdljrDvKKEHUcJhMLq9iaiAxTlr4e"
        );

        for (String hash : hashes) {
            assertTrue(passwordEncoder.matches("123456", hash));
        }
    }

    /**
     * 相同明文每次加密结果不同（随机盐），但都能校验通过
     */
    @Test
    void encodeShouldUseRandomSalt() {
        String first = passwordEncoder.encode("123456");
        String second = passwordEncoder.encode("123456");

        assertNotEquals(first, second);
        assertTrue(passwordEncoder.matches("123456", first));
        assertTrue(passwordEncoder.matches("123456", second));
    }
}
