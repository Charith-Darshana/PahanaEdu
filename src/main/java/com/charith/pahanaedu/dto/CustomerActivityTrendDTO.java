package com.charith.pahanaedu.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CustomerActivityTrendDTO {
    private String period;
    private Long newCustomers;
    private Long returningCustomers;
    private Long totalOrders;
    private BigDecimal totalRevenue;
}
