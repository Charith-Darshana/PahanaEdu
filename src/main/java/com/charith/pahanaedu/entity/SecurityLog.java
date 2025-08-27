package com.charith.pahanaedu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String action;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
