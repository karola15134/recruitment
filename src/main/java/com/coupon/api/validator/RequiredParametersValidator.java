package com.coupon.api.validator;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RequiredParametersValidator {

    public List<String> getMissingParams(
            Map<String, String> allParams,
            List<String> requiredParameters){

        return requiredParameters.stream()
                .filter(p -> !allParams.containsKey(p)
                        || allParams.get(p) == null
                        || allParams.get(p).isEmpty())
                .toList();
    }
}
