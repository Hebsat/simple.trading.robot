package com.simple.trading.robot.service.impl;

import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.service.CandleService;
import com.simple.trading.robot.service.api.ApiSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandleServiceImpl implements CandleService {

    private final ApiSelector apiSelector;

    @Override
    public List<Candle> getInstrumentCandles(InstrumentInfoRequest instrumentInfoRequest) {
        return apiSelector.getApiByType(instrumentInfoRequest.getApi()).getInstrumentHistory(instrumentInfoRequest);
    }
}
