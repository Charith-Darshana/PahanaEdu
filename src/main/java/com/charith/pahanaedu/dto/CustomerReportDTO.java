package com.charith.pahanaedu.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CustomerReportDTO {
    private Integer customerId;
    private String customerName;
    private BigDecimal totalSpent;
    private Long orderCount;
    private String lastOrderDate;
    private BigDecimal averageOrderValue;
    private Long totalQuantityPurchased;
}
