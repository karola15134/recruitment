package com.coupon.api.controller;

import com.coupon.api.dto.CouponDTO;
import com.coupon.api.exception.CouponAlreadyExistsException;
import com.coupon.api.exception.DifferentCouponCountryException;
import com.coupon.api.exception.MissingCouponException;
import com.coupon.api.exception.ReedemCountExceededException;
import com.coupon.api.service.CouponService;
import com.coupon.api.service.GeoIpService;
import com.coupon.api.validator.RequiredParametersValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class CouponController {

    private final CouponService couponService;
    private final GeoIpService geoIpService;
    private final RequiredParametersValidator requiredParametersValidator;

    private static final List<String> CREATE_COUPON_PARAMS = List.of("code", "maxUses", "country");
    private static final List<String> REDEEM_COUPON_PARAMS = List.of("code");

    public CouponController(
            CouponService couponService,
            GeoIpService geoIpService,
            RequiredParametersValidator requiredParametersValidator) {
        this.couponService = couponService;
        this.geoIpService = geoIpService;
        this.requiredParametersValidator = requiredParametersValidator;
    }

    @RequestMapping("/createCoupon")
    public ResponseEntity<String> createCoupon(@RequestParam Map<String, String> allParams) {
        ResponseEntity<String> validationError = validateParams(allParams, CREATE_COUPON_PARAMS);
        if (validationError != null) return validationError;

        String code = allParams.get("code");
        long maxUses = Long.parseLong(allParams.get("maxUses"));
        String country = allParams.get("country");

        CouponDTO newCoupon = new CouponDTO(code, maxUses, country);

        try {
            couponService.createCoupon(newCoupon);
        } catch (CouponAlreadyExistsException e) {
            return plainTextResponse("Coupon cannot be created. Coupon with provided code already exists.", false);
        }

        return plainTextResponse("Coupon has been properly created", true);
    }

    @RequestMapping("/redeemCoupon")
    public ResponseEntity<String> redeemCoupon(
            @RequestParam Map<String, String> allParams,
            @RequestAttribute("CLIENT_IP") String clientIp) {

        ResponseEntity<String> validationError = validateParams(allParams, REDEEM_COUPON_PARAMS);
        if (validationError != null) return validationError;

        String couponCode = allParams.get("code");
        String clientCountry = geoIpService.getCountry(clientIp);
        log.info("Redeeming coupon {} for client IP: {}, country: {}", couponCode, clientIp, clientCountry);

        try {
            couponService.redeemCoupon(couponCode, clientCountry);
        } catch (MissingCouponException e) {
            return plainTextResponse("Coupon does not exist", false);
        } catch (ReedemCountExceededException e) {
            return plainTextResponse("Redeem limit exceeded for coupon", false);
        } catch (DifferentCouponCountryException e) {
            return plainTextResponse("Coupon is for a different country", false);
        } catch (Exception e) {
            log.error("Unexpected error redeeming coupon", e);
            return plainTextResponse("Internal error during coupon redemption", false);
        }

        return plainTextResponse("Coupon has been properly redeemed", true);
    }


    private ResponseEntity<String> validateParams(Map<String, String> params, List<String> requiredParams) {
        List<String> missing = requiredParametersValidator.getMissingParams(params, requiredParams);
        if (!missing.isEmpty()) {
            return plainTextResponse("Coupon cannot be processed. Required parameters missing: " + missing, false);
        }
        return null;
    }

    private ResponseEntity<String> plainTextResponse(String body, boolean ok) {
        return ok
                ? ResponseEntity.ok().header("Content-Type", "text/plain").body(body)
                : ResponseEntity.unprocessableContent().header("Content-Type", "text/plain").body(body);
    }
}