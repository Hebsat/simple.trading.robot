package com.simple.trading.robot.dto.properties;

import com.simple.trading.robot.service.api.ApiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTemplate {

    private ApiType api;
    private String id;
    private BigDecimal maxSum = BigDecimal.ZERO;
    private BigDecimal maxPercent = BigDecimal.valueOf(100);
    private boolean isSandbox;
}
