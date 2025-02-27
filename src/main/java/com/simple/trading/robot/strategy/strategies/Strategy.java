package com.simple.trading.robot.strategy.strategies;

import com.simple.trading.robot.dto.strategy.InitializationRequest;
import com.simple.trading.robot.dto.strategy.InitializationResponse;
import com.simple.trading.robot.dto.OrderCommand;
import com.simple.trading.robot.dto.strategy.StrategyType;
import com.simple.trading.robot.entity.Candle;

import java.util.List;

public interface Strategy {

    String getName();

    List<String> getInstruments();

    OrderCommand handleInstantInfo(List<Candle> info);

    void initializeStrategy(InitializationResponse initializeInfo);

    InitializationRequest getInitializeInfo();

    boolean checkType(StrategyType strategyType);
}
