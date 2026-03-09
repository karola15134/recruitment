package com.empik.recruitment.controller;

import com.empik.recruitment.dto.CouponDTO;
import com.empik.recruitment.exception.CouponCodeException;
import com.empik.recruitment.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class CouponController {

    @Autowired
    @Qualifier("couponService")
    private final CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @RequestMapping("/api/createCoupon")
    @ResponseBody
    public ResponseEntity<String> createCoupon(@RequestParam Map<String,String> allParams) {
        String couponCodeParam = allParams.get("code");
        String maxUsesParam = allParams.get("maxUses");
        String countryParam = allParams.get("country");

        if(couponCodeParam == null){
            return new ResponseEntity<>("Coupon cannot be created. Missing code parameter", HttpStatus.UNPROCESSABLE_CONTENT);
        }

        if(maxUsesParam == null){
            return new ResponseEntity<>("Coupon cannot be created. Missing maxUses parameter", HttpStatus.UNPROCESSABLE_CONTENT);
        }

        CouponDTO newCoupon = new CouponDTO(couponCodeParam, new BigDecimal(maxUsesParam),countryParam);
        String jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newCoupon);

        try {
            couponService.createCoupon(newCoupon);
        } catch (CouponCodeException e) {
            return  ResponseEntity
                     .unprocessableContent()
                     .header("Content-Type", "text/plain")
                     .body("Coupon cannot be created. \nCoupon with provided code already exists.");
        }

        return ResponseEntity
                .ok()
                .header("Content-Type", "text/plain")
                .body("OK - Coupon has been properly created\n" + jsonStr);

    }
}
