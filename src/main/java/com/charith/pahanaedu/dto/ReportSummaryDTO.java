package com.charith.pahanaedu.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ReportSummaryDTO {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalQuantitySold;
    private BigDecimal averageOrderValue;
    private Long totalCustomers;
    private Long totalItems;
}
