package org.example.demo222.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Customer {
    private Long id;
    private String name;
    private String phone;
    private String address;
    private String preference;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}