package com.empik.recruitment.service;

import com.empik.recruitment.dto.CouponDTO;
import com.empik.recruitment.exception.CouponAlreadyExistsException;
import com.empik.recruitment.exception.DifferentCouponCountryException;
import com.empik.recruitment.exception.MissingCouponException;
import com.empik.recruitment.exception.ReedemCountExceededException;
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
    public void createCoupon(CouponDTO newCouponDTO)
            throws CouponAlreadyExistsException {
        Optional<CouponDTO> queryResult = couponRepository.findByCodeIgnoreCase(newCouponDTO.getCode());
        log.info("Code: {}", newCouponDTO.getCode());
        if(queryResult.isPresent()){
            throw new CouponAlreadyExistsException();
        }
        else {
            couponRepository.save(newCouponDTO);
            log.info("New coupon created");
        }
    }

    @Override
    public void redeemCoupon(String couponCode, String countryParam)
            throws MissingCouponException, ReedemCountExceededException, DifferentCouponCountryException {

        Optional<CouponDTO> coupon = couponRepository.findByCodeIgnoreCase(couponCode);
        if(coupon.isEmpty()){
            throw new MissingCouponException();
        }

        CouponDTO couponData = coupon.get();
        Long currentUses = couponData.getCurrentUses();
        Long maxUses = couponData.getMaxUses();
        String country =  couponData.getCountry();

        log.info("""
                        Coupon:
                        code: {}
                        current uses: {}
                        max uses: {}
                        country: {}"""
                ,couponData.getCode()
                ,currentUses
                ,maxUses
                ,country);

        if(!country.equals(countryParam) && !countryParam.equals("LOCAL")){
            throw new DifferentCouponCountryException();
        }

        if(currentUses >= maxUses){
            throw new ReedemCountExceededException();
        }

        couponData.setCurrentUses(couponData.getCurrentUses() + 1);
        couponRepository.save(couponData);

    }


}
