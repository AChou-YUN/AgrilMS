package org.example.demo222.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 */
@Data
public class SysUser {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}