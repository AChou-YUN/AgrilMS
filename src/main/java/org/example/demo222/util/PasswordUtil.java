package org.example.demo222.util;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码工具类（基于BCrypt，使用Hutool实现）
 */
public class PasswordUtil {

    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 校验密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
