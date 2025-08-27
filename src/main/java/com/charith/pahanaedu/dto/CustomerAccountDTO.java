package com.charith.pahanaedu.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerAccountDTO {
    private Integer customerId;
    private String customerName;
    private Integer totalUnits;
    private BigDecimal totalSpent;
}
