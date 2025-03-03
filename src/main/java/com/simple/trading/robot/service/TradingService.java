package com.simple.trading.robot.service;

import com.simple.trading.robot.dto.api.Candle;

public interface TradingService {

    void handleCurrentPrice(Candle candle);
}
