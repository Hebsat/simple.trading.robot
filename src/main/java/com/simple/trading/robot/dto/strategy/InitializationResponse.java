package com.simple.trading.robot.dto.strategy;

import com.simple.trading.robot.entity.Candle;
import com.simple.trading.robot.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class InitializationResponse {

    private Map<InstrumentInfoRequest, List<Candle>> instrumentCandles;
    private List<Order> orders;
}
