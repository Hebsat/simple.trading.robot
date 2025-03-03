package com.simple.trading.robot.service.impl;

import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.service.TradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingServiceImpl implements TradingService {

    @Override
    public void handleCurrentPrice(Candle candle) {
        log.warn("I've got a candle: {}", candle);
    }
}
