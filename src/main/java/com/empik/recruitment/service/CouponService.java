package com.empik.recruitment.service;

import com.empik.recruitment.dto.CouponDTO;
import com.empik.recruitment.exception.CouponAlreadyExistsException;
import com.empik.recruitment.exception.DifferentCouponCountryException;
import com.empik.recruitment.exception.MissingCouponException;
import com.empik.recruitment.exception.ReedemCountExceededException;

public interface CouponService {

     void createCoupon(CouponDTO newCouponDTO) throws CouponAlreadyExistsException;
     void redeemCoupon(String couponCode, String country) throws MissingCouponException, ReedemCountExceededException, DifferentCouponCountryException;
}
