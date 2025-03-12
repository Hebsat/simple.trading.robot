package com.simple.trading.robot.strategy;

import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.service.api.ApiType;
import com.simple.trading.robot.strategy.strategies.Strategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StrategyService {

    Strategy getStrategy(String name);

    Map<ApiType, Set<Instrument>> getAllInstrumentsByApi();

    List<Strategy> getStrategiesByInstrument(Instrument instrument);

    Map<String, Strategy> getAllStrategies();
}
