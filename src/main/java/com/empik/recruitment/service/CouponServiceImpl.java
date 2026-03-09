package com.empik.recruitment.service;

import com.empik.recruitment.dto.CouponDTO;
import com.empik.recruitment.exception.CouponCodeException;
import com.empik.recruitment.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service("couponService")
public class CouponServiceImpl implements CouponService{

    @Autowired
    private final CouponRepository couponRepository;


    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public void createCoupon(CouponDTO newCouponDTO) throws CouponCodeException {
        Optional<CouponDTO> queryResult = findCouponByCode(newCouponDTO.getCode());
        log.info("Code: {}", newCouponDTO.getCode());
        if(queryResult.isPresent()){
            throw new CouponCodeException();
        }
        else {
            couponRepository.save(newCouponDTO);
            log.info("New coupon created");
        }
    }

    @Override
    public void redeemCoupon(CouponDTO couponDTO) {
        couponRepository.save(couponDTO);
    }

    private Optional<CouponDTO> findCouponByCode(String code ) {
        return couponRepository.findByCode(code);
    }

}
