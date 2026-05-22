package org.example.demo222.dto.request;

import lombok.Data;

/**
 * 更新用户信息请求DTO
 */
@Data
public class UserUpdateRequest {
    private String realName;
    private String phone;
    private String email;
}