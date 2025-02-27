package com.simple.trading.robot.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Candle {

    private long id;
    private String version;
    private String instrument;
    private BigDecimal openingPrice;
    private BigDecimal closingPrice;
    private BigDecimal highestPrice;
    private BigDecimal lowestPrice;
    private OffsetDateTime openingTime;
    private String interval;
}
