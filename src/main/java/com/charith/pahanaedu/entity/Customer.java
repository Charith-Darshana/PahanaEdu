package com.charith.pahanaedu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account_no", unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 10)
    private String telephone;

    @Column(nullable = false)
    private Integer units;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
