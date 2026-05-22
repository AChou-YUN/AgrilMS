package org.example.demo222.dto.response;

import lombok.Data;

/**
 * 角色视图对象
 */
@Data
public class RoleVO {
    private Long id;
    private String roleName;
    private String roleKey;
    private String description;
}