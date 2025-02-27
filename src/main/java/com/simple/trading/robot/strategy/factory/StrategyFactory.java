package com.simple.trading.robot.strategy.factory;

import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.strategy.strategies.Strategy;

public interface StrategyFactory {

    Strategy createStrategy(StrategyTemplate strategyTemplate);
}
