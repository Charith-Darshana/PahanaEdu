package com.charith.pahanaedu.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BillDTO {
    private Integer id;
    private Integer customerId;
    private String customerAccountNumber;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private LocalDateTime dateTime;
    private BigDecimal total;
    private List<BillItemDTO> items;

    @Data
    @Builder
    public static class BillItemDTO {
        private Integer id;
        private String name;
        private String description;
        private Integer quantity;
        private String unitPrice;
        private String subtotal;
    }
}
