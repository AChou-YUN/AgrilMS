package org.example.demo222.dto.request;

import lombok.Data;

/**
 * 用户查询请求DTO
 */
@Data
public class UserQueryRequest {
    private String username;
    private String realName;
    private Integer status;
    private Long roleId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}