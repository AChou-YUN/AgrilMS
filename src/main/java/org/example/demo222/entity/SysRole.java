package org.example.demo222.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@Data
public class SysRole {
    private Long id;
    private String roleName;
    private String roleKey;
    private String description;
    private LocalDateTime createTime;
}