package com.simple.trading.robot.dto.api;

import com.simple.trading.robot.dto.CandleInterval;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@ToString
public class Candle {

    private String instrument;
    private BigDecimal openingPrice;
    private BigDecimal closingPrice;
    private BigDecimal highestPrice;
    private BigDecimal lowestPrice;
    private OffsetDateTime openingTime;
    private CandleInterval interval;
}
