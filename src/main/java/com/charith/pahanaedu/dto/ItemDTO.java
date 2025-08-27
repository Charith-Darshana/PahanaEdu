package com.charith.pahanaedu.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemDTO {
    private String name;
    private String description;
    private String categoryName;
    private String authorName;
    private String publisherName;
    private String isbn;
    private BigDecimal price;
    private Integer quantity;
}
