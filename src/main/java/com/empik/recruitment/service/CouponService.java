package com.empik.recruitment.service;

import com.empik.recruitment.dto.CouponDTO;
import com.empik.recruitment.exception.CouponCodeException;

import java.util.Optional;

public interface CouponService {

     void createCoupon(CouponDTO newCouponDTO) throws CouponCodeException;
     void redeemCoupon(CouponDTO couponDTO);
}
