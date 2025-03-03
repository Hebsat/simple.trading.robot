package com.simple.trading.robot.strategy;

import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.service.api.ApiType;

import java.util.List;
import java.util.Map;

public interface StrategyPrepareService {

    List<StrategyTemplate> getStrategies();

    Map<ApiType, Map<String, List<StrategyTemplate>>> getStrategiesByApiByAccounts();
}
