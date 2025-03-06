package com.simple.trading.robot.strategy.strategies;

import com.simple.trading.robot.dto.OrderCommand;
import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.dto.strategy.InitializationRequest;
import com.simple.trading.robot.dto.strategy.InitializationResponse;
import com.simple.trading.robot.dto.strategy.StrategyType;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.service.api.ApiType;

import java.util.List;

public interface Strategy {

    String getName();

    ApiType getApi();

    Instrument getInstrument();

    OrderCommand handleInstantInfo(List<Candle> info);

    void initializeStrategy(InitializationResponse initializeInfo);

    InitializationRequest getInitializeInfo();

    boolean checkType(StrategyType strategyType);
}
