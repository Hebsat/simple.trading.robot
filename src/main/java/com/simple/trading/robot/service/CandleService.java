package com.simple.trading.robot.service;

import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.dto.api.Candle;

import java.util.List;

public interface CandleService {

    List<Candle> getInstrumentCandles(InstrumentInfoRequest instrumentInfoRequest);
}
