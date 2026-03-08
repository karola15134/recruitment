package com.empik.recruitment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupon")
@Setter
@Getter
public class Coupon {
    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "max_uses", nullable = false)
    private Integer maxUses;

    @Column(name = "current_uses", nullable = false)
    private Integer currentUses = 0;

    @Column(name = "country", nullable = false, length = 2)
    private String country;
}
