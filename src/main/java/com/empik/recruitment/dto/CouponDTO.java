package com.empik.recruitment.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "coupon")
@Setter
@Getter
public class CouponDTO {
    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "max_uses", nullable = false)
    private BigDecimal maxUses =  BigDecimal.ZERO;

    @Column(name = "current_uses", nullable = false)
    private BigDecimal currentUses = BigDecimal.ZERO;

    @Column(name = "country", nullable = false, length = 2)
    private String country;

    public CouponDTO() {
    }

    public CouponDTO(String code, BigDecimal maxUses, String country) {
        this.code = code;
        this.maxUses = maxUses;
        this.country = country;
    }

    @Override
    public String toString() {
        return "CouponDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", createdAt=" + createdAt +
                ", maxUses=" + maxUses +
                ", currentUses=" + currentUses +
                ", country='" + country + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CouponDTO couponDTO = (CouponDTO) o;
        return Objects.equals(id, couponDTO.id) && Objects.equals(code, couponDTO.code) && Objects.equals(createdAt, couponDTO.createdAt) && Objects.equals(maxUses, couponDTO.maxUses) && Objects.equals(currentUses, couponDTO.currentUses) && Objects.equals(country, couponDTO.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, createdAt, maxUses, currentUses, country);
    }
}
