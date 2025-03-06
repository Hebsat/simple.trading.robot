package com.simple.trading.robot.dto.properties;

import com.simple.trading.robot.service.api.ApiType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountTemplate {

    private ApiType api;
    private String id;
    private BigDecimal maxSum = BigDecimal.ZERO;
    private BigDecimal maxPercent = BigDecimal.valueOf(100);
    private boolean isSandbox;
}
