package org.example.demo222.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 计量单位实体类
 */
@Data
public class Unit {
    private Long id;
    private String name;
    private String abbreviation;
    private LocalDateTime createTime;
}