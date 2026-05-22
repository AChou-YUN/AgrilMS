package org.example.demo222.entity;

import lombok.Data;

/**
 * 用户角色关联实体类
 */
@Data
public class SysUserRole {
    private Long id;
    private Long userId;
    private Long roleId;
}