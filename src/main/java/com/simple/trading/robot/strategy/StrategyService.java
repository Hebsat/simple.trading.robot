package com.simple.trading.robot.strategy;

import com.simple.trading.robot.strategy.strategies.Strategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StrategyService {

    Strategy getStrategy(String name);

    Set<String> getAllInstruments();

    Map<String, List<String>> getStrategiesInstruments();

    Map<String, Strategy> getAllStrategies();
}
