package org.example.demo222.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户响应VO
 */
@Data
public class CustomerVO {
    private Long id;
    private String name;
    private String phone;
    private String address;
    private String preference;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}