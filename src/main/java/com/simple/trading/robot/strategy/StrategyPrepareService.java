package com.simple.trading.robot.strategy;

import com.simple.trading.robot.dto.properties.StrategyTemplate;

import java.util.List;
import java.util.Map;

public interface StrategyPrepareService {

    List<StrategyTemplate> getStrategies();

    Map<String, List<StrategyTemplate>> getStrategiesByAccounts();
}
