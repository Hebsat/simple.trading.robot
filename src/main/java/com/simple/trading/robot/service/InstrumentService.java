package com.simple.trading.robot.service;

import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;

public interface InstrumentService {

    void updateInstrument(InstrumentInfoRequest instrumentInfo);
}
