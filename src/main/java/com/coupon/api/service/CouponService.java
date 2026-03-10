package com.coupon.api.service;

import com.coupon.api.dto.CouponDTO;
import com.coupon.api.exception.CouponAlreadyExistsException;
import com.coupon.api.exception.DifferentCouponCountryException;
import com.coupon.api.exception.MissingCouponException;
import com.coupon.api.exception.ReedemCountExceededException;

public interface CouponService {

     void createCoupon(CouponDTO newCouponDTO) throws CouponAlreadyExistsException;
     void redeemCoupon(String couponCode, String country) throws MissingCouponException, ReedemCountExceededException, DifferentCouponCountryException;
}
