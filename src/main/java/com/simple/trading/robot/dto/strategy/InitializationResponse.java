package com.simple.trading.robot.dto.strategy;

import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.entity.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
public class InitializationResponse {

    private Map<InstrumentInfoRequest, List<Candle>> instrumentCandles;
    private List<Order> orders;
}
