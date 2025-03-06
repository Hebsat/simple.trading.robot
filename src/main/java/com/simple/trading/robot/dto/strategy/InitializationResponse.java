package com.simple.trading.robot.dto.strategy;

import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.entity.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class InitializationResponse {

    private Instrument instrument;
    private List<Candle> instrumentCandles;
    private List<Order> orders;
}
