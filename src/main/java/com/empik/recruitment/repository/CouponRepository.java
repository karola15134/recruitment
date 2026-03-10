package com.empik.recruitment.repository;

import com.empik.recruitment.dto.CouponDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponDTO, Long> {
     Optional<CouponDTO> findByCodeIgnoreCase(String code);
}
