package com.simple.trading.robot.service.impl;

import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.entity.Candle;
import com.simple.trading.robot.service.CandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandleServiceImpl implements CandleService {

    @Override
    public List<Candle> getInstrumentCandles(InstrumentInfoRequest instrumentInfoRequest) {
        return List.of();
    }
}
