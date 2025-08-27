package com.charith.pahanaedu.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TopPerformingItemDTO {
    private Integer itemId;
    private String itemName;
    private Long totalQuantitySold;
    private BigDecimal totalRevenue;
    private Long orderCount;
}
