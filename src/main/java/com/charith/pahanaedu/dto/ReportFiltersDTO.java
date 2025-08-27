package com.charith.pahanaedu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReportFiltersDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String period; // day, week, month, quarter, year
    private Integer customerId;
    private Integer itemId;
}
