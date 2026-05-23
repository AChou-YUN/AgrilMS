package org.example.demo222.dto.request;

import lombok.Data;
import java.time.LocalDate;

/**
 * 报表查询请求DTO
 */
@Data
public class ReportQueryRequest {
    private String type;          // daily/monthly/yearly
    private Integer year;
    private Integer month;
    private LocalDate startDate;
    private LocalDate endDate;
}