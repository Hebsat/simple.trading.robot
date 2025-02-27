package com.simple.trading.robot.strategy;

import com.simple.trading.robot.configuraton.SimpleTradingRobotProperties;
import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.exception.SimpleTradingRobotPropertiesException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyPrepareServiceImpl implements StrategyPrepareService {

    private final SimpleTradingRobotProperties properties;

    private final Map<String, StrategyTemplate> strategiesByNames = new HashMap<>();
    @Getter(onMethod_ = @Override)
    private final Map<String, List<StrategyTemplate>> strategiesByAccounts = new HashMap<>();

    @PostConstruct
    public void init() {
        log.trace("Все найденные стратегии: {}", properties.getStrategies());
        properties.getStrategies().stream().filter(StrategyTemplate::isEnabled).forEach(this::validateStrategy);
    }

    private void validateStrategy(StrategyTemplate strategyTemplate) {
        if (strategiesByNames.containsKey(strategyTemplate.getName())) {
            throw new SimpleTradingRobotPropertiesException("Каждая стратегия должна иметь уникальное имя");
        }
        strategiesByNames.put(strategyTemplate.getName(), strategyTemplate);
        strategiesByAccounts.computeIfAbsent(strategyTemplate.getAccount().getId(), k -> new ArrayList<>()).add(strategyTemplate);
    }

    @Override
    public List<StrategyTemplate> getStrategies() {
        return strategiesByNames.values().stream().toList();
    }
}
