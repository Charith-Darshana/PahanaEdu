package com.charith.pahanaedu.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InventoryReportDTO {
    private Integer itemId;
    private String itemName;
    private String description;
    private Integer currentStock;
    private Long totalSold;
    private BigDecimal totalRevenue;
    private BigDecimal unitPrice;
    private BigDecimal stockValue;
    private String lastSoldDate;
}
