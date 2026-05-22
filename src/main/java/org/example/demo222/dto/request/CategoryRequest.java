package org.example.demo222.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分类请求DTO
 */
@Data
public class CategoryRequest {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    private String description;
    private Integer sortOrder;
}