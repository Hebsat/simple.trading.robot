package com.simple.trading.robot.dto.properties;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDto {

    private String id;
    private BigDecimal maxSum = BigDecimal.ZERO;
    private BigDecimal maxPercent = BigDecimal.valueOf(100);
    private boolean isSandbox;
}
