package com.simple.trading.robot.dto.api;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountInfo {

    private String accountId;
    private String currency;
    private BigDecimal amount;
}
