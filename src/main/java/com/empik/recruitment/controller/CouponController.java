package com.empik.recruitment.controller;

import com.empik.recruitment.dto.CouponDTO;
import com.empik.recruitment.exception.CouponAlreadyExistsException;
import com.empik.recruitment.exception.DifferentCouponCountryException;
import com.empik.recruitment.exception.MissingCouponException;
import com.empik.recruitment.exception.ReedemCountExceededException;
import com.empik.recruitment.service.CouponService;
import com.empik.recruitment.service.GeoIpService;
import com.empik.recruitment.validator.RequiredParametersValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class CouponController {

    @Autowired
    @Qualifier("couponService")
    private final CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RequiredParametersValidator requiredParametersValidator;

    public CouponController(CouponService couponService, GeoIpService geoIpService) {
        this.couponService = couponService;
        this.geoIpService = geoIpService;
    }

    private final GeoIpService geoIpService;

    @RequestMapping("/api/createCoupon")
    @ResponseBody
    public ResponseEntity<String> createCoupon(@RequestParam Map<String,String> allParams) {

        List<String> createCouponParams = List.of("code","maxUses","country");
        List<String> missingRequiredParams = requiredParametersValidator.getMissingParams(allParams,createCouponParams);

        if(!missingRequiredParams.isEmpty()){
            return ResponseEntity
                    .unprocessableContent()
                    .header("Content-Type", "text/plain")
                    .body("Coupon cannot be created.\nRequired Parameters are missing\n " + missingRequiredParams);
        }
        String couponCodeParam = allParams.get("code");
        String maxUsesParam = allParams.get("maxUses");
        String countryParam = allParams.get("country");
        log.info("Uses: {}", Long.valueOf(maxUsesParam));
        CouponDTO newCoupon = new CouponDTO(couponCodeParam, Long.valueOf(maxUsesParam),countryParam);
        String jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newCoupon);

        try {
            couponService.createCoupon(newCoupon);
        } catch (CouponAlreadyExistsException e) {
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

    @RequestMapping("/api/redeemCoupon")
    @ResponseBody
    public ResponseEntity<String> redeemCoupon( @RequestParam Map<String,String> allParams, @RequestAttribute("CLIENT_IP") String clientIp) {
        List<String> updateCouponParams = List.of("code");
        List<String> missingRequiredParams = requiredParametersValidator.getMissingParams(allParams,updateCouponParams);
        log.info("Client IP: {}",clientIp);

        if(!missingRequiredParams.isEmpty()){
            return ResponseEntity
                    .unprocessableContent()
                    .header("Content-Type", "text/plain")
                    .body("Coupon cannot be created.\nRequired Parameters are missing\n " + missingRequiredParams);
        }
        String couponCodeParam = allParams.get("code");
        try {
            log.info("IP country: {}",geoIpService.getCountry(clientIp));
            couponService.redeemCoupon(couponCodeParam, geoIpService.getCountry(clientIp));
        } catch (MissingCouponException e) {
            return ResponseEntity
                    .unprocessableContent()
                    .header("Content-Type", "text/plain")
                    .body("Coupon cannot be created.\nCoupon {} does not exits\n " + couponCodeParam);
        } catch (ReedemCountExceededException e) {
            return ResponseEntity
                    .unprocessableContent()
                    .header("Content-Type", "text/plain")
                    .body("Coupon cannot be created.\nLimit of redeeming coupon {} exceeded\n " + couponCodeParam);
        }
        catch (DifferentCouponCountryException e) {
         return ResponseEntity
                 .unprocessableContent()
                 .header("Content-Type", "text/plain")
                 .body("Coupon cannot be created.\nCoupon has been created for different country\n " + couponCodeParam);
     }
     catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity
                .ok()
                .header("Content-Type", "text/plain")
                .body("OK - Coupon has been properly redeemed\n") ;
    }
}
