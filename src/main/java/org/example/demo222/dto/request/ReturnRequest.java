package org.example.demo222.dto.request;

import lombok.Data;
import java.util.List;

/**
 * 退货请求DTO
 */
@Data
public class ReturnRequest {
    private String remark;
    private List<ReturnItemRequest> items; // 为空则整单退货
}