package com.coupon.api.service;

import com.coupon.api.dto.CouponDTO;
import com.coupon.api.exception.CouponAlreadyExistsException;
import com.coupon.api.exception.DifferentCouponCountryException;
import com.coupon.api.exception.MissingCouponException;
import com.coupon.api.exception.ReedemCountExceededException;
import com.coupon.api.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service("couponService")
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public void createCoupon(CouponDTO newCouponDTO) throws CouponAlreadyExistsException {
        log.info("Attempting to create coupon with code: {}", newCouponDTO.getCode());

        if (couponRepository.findByCodeIgnoreCase(newCouponDTO.getCode()).isPresent()) {
            log.warn("Coupon with code {} already exists", newCouponDTO.getCode());
            throw new CouponAlreadyExistsException();
        }

        couponRepository.save(newCouponDTO);
        log.info("New coupon created: {}", newCouponDTO.getCode());
    }

    @Override
    public void redeemCoupon(String couponCode, String clientCountry)
            throws MissingCouponException, ReedemCountExceededException, DifferentCouponCountryException {

        CouponDTO coupon = fetchCouponOrThrow(couponCode);

        log.info("""
                Redeeming coupon:
                code: {}
                current uses: {}
                max uses: {}
                country: {}
                """, coupon.getCode(), coupon.getCurrentUses(), coupon.getMaxUses(), coupon.getCountry());

        validateCountry(coupon, clientCountry);
        validateUsage(coupon);

        coupon.setCurrentUses(coupon.getCurrentUses() + 1);
        couponRepository.save(coupon);

        log.info("Coupon {} redeemed successfully. Current uses: {}", coupon.getCode(), coupon.getCurrentUses());
    }


    private CouponDTO fetchCouponOrThrow(String couponCode) throws MissingCouponException {
        Optional<CouponDTO> optionalCoupon = couponRepository.findByCodeIgnoreCase(couponCode);
        if (optionalCoupon.isEmpty()) {
            log.warn("Coupon {} not found", couponCode);
            throw new MissingCouponException();
        }
        return optionalCoupon.get();
    }

    private void validateCountry(CouponDTO coupon, String clientCountry) throws DifferentCouponCountryException {
        if (!coupon.getCountry().equals(clientCountry) && !"LOCAL".equals(clientCountry)) {
            log.warn("Coupon {} is for a different country: {}", coupon.getCode(), coupon.getCountry());
            throw new DifferentCouponCountryException();
        }
    }

    private void validateUsage(CouponDTO coupon) throws ReedemCountExceededException {
        if (coupon.getCurrentUses() >= coupon.getMaxUses()) {
            log.warn("Coupon {} exceeded max uses: {}/{}", coupon.getCode(), coupon.getCurrentUses(), coupon.getMaxUses());
            throw new ReedemCountExceededException();
        }
    }
}