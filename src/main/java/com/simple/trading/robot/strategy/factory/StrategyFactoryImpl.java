package com.simple.trading.robot.strategy.factory;

import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.dto.strategy.StrategyType;
import com.simple.trading.robot.strategy.strategies.AnotherTradingStrategy;
import com.simple.trading.robot.strategy.strategies.SimpleTradingStrategy;
import com.simple.trading.robot.strategy.strategies.Strategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import static com.simple.trading.robot.dto.strategy.StrategyType.ANOTHER_STRATEGY;
import static com.simple.trading.robot.dto.strategy.StrategyType.SIMPLE_STRATEGY;

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyFactoryImpl implements StrategyFactory {

    private static final Map<StrategyType, Function<StrategyTemplate, Strategy>> STRATEGY_CREATOR = new EnumMap<>(
            Map.of(
                    SIMPLE_STRATEGY, SimpleTradingStrategy::new,
                    ANOTHER_STRATEGY, AnotherTradingStrategy::new
            )
    );

    @Override
    public Strategy createStrategy(StrategyTemplate strategyTemplate) {
        log.debug("Создается стратегия {}", strategyTemplate.getName());
        return STRATEGY_CREATOR
                .get(strategyTemplate.getStrategyType())
                .apply(strategyTemplate);
    }
}
