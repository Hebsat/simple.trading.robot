package com.simple.trading.robot.dto.api;

import com.simple.trading.robot.entity.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountInfo {

    private String accountId;
    private Currency currency;
    private BigDecimal amount;
}
