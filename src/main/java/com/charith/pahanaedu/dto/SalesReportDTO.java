package com.charith.pahanaedu.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SalesReportDTO {
    private String period;
    private BigDecimal totalAmount;
    private Long totalQuantity;
    private Long orderCount;
    private BigDecimal averageOrderValue;
    private String topSellingItem;
}
