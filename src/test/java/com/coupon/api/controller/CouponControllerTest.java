package com.coupon.api.controller;

import com.coupon.api.exception.CouponAlreadyExistsException;
import com.coupon.api.exception.DifferentCouponCountryException;
import com.coupon.api.exception.MissingCouponException;
import com.coupon.api.exception.ReedemCountExceededException;
import com.coupon.api.service.CouponService;
import com.coupon.api.service.GeoIpService;
import com.coupon.api.validator.RequiredParametersValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @MockitoBean
    private GeoIpService geoIpService;

    @MockitoBean
    private RequiredParametersValidator requiredParametersValidator;

    @Test
    void shouldCreateCouponSuccessfully() throws Exception {

        Mockito.when(requiredParametersValidator.getMissingParams(any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/createCoupon")
                        .param("code", "TEST10")
                        .param("maxUses", "5")
                        .param("country", "PL"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon has been properly created"));

        Mockito.verify(couponService).createCoupon(any());
    }

    @Test
    void shouldReturnErrorWhenCouponAlreadyExists() throws Exception {

        Mockito.when(requiredParametersValidator.getMissingParams(any(), any()))
                .thenReturn(List.of());

        Mockito.doThrow(new CouponAlreadyExistsException())
                .when(couponService).createCoupon(any());

        mockMvc.perform(get("/api/createCoupon")
                        .param("code", "TEST10")
                        .param("maxUses", "5")
                        .param("country", "PL"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Coupon cannot be created. Coupon with provided code already exists."));
    }

    @Test
    void shouldReturnErrorWhenParamsMissing() throws Exception {

        Mockito.when(requiredParametersValidator.getMissingParams(any(), any()))
                .thenReturn(List.of("country"));

        mockMvc.perform(get("/api/createCoupon")
                        .param("code", "TEST10"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Coupon cannot be processed. Required parameters missing: [country]"));
    }

    @Test
    void shouldRedeemCouponSuccessfully() throws Exception {

        Mockito.when(requiredParametersValidator.getMissingParams(any(), any()))
                .thenReturn(List.of());

        Mockito.when(geoIpService.getCountry("127.0.0.1"))
                .thenReturn("PL");

        mockMvc.perform(get("/api/redeemCoupon")
                        .param("code", "TEST10")
                        .requestAttr("CLIENT_IP", "127.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon has been properly redeemed"));

        Mockito.verify(couponService).redeemCoupon("TEST10", "PL");
    }

    @Test
    void shouldReturnErrorWhenCouponMissing() throws Exception {

        Mockito.when(requiredParametersValidator.getMissingParams(any(), any()))
                .thenReturn(List.of());

        Mockito.when(geoIpService.getCountry("127.0.0.1"))
                .thenReturn("PL");

        Mockito.doThrow(new MissingCouponException())
                .when(couponService).redeemCoupon(eq("TEST10"), eq("PL"));

        mockMvc.perform(get("/api/redeemCoupon")
                        .param("code", "TEST10")
                        .requestAttr("CLIENT_IP", "127.0.0.1"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Coupon does not exist"));
    }

    @Test
    void shouldReturnErrorWhenRedeemLimitExceeded() throws Exception {

        Mockito.when(requiredParametersValidator.getMissingParams(any(), any()))
                .thenReturn(List.of());

        Mockito.when(geoIpService.getCountry("127.0.0.1"))
                .thenReturn("PL");

        Mockito.doThrow(new ReedemCountExceededException())
                .when(couponService).redeemCoupon(eq("TEST10"), eq("PL"));

        mockMvc.perform(get("/api/redeemCoupon")
                        .param("code", "TEST10")
                        .requestAttr("CLIENT_IP", "127.0.0.1"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Redeem limit exceeded for coupon"));
    }

    @Test
    void shouldReturnErrorWhenCountryDifferent() throws Exception {

        Mockito.when(requiredParametersValidator.getMissingParams(any(), any()))
                .thenReturn(List.of());

        Mockito.when(geoIpService.getCountry("127.0.0.1"))
                .thenReturn("US");

        Mockito.doThrow(new DifferentCouponCountryException())
                .when(couponService).redeemCoupon(eq("TEST10"), eq("US"));

        mockMvc.perform(get("/api/redeemCoupon")
                        .param("code", "TEST10")
                        .requestAttr("CLIENT_IP", "127.0.0.1"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Coupon is for a different country"));
    }
}