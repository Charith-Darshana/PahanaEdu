package com.charith.pahanaedu.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String role;
    private LocalDateTime createdAt;
}
