package org.example.demo222.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 供应商响应VO
 */
@Data
public class SupplierVO {
    private Long id;
    private String name;
    private String contactPerson;
    private String phone;
    private String address;
    private Integer creditLevel;
    private String bankAccount;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}