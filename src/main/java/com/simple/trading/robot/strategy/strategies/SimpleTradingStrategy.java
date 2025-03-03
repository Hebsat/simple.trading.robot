package com.simple.trading.robot.strategy.strategies;

import com.simple.trading.robot.dto.CandleInterval;
import com.simple.trading.robot.dto.OrderCommand;
import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.dto.strategy.InitializationRequest;
import com.simple.trading.robot.dto.strategy.InitializationResponse;
import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.dto.strategy.StrategyType;
import com.simple.trading.robot.dto.api.Candle;

import java.time.Duration;
import java.util.List;

public class SimpleTradingStrategy extends AbstractStrategy {

    public SimpleTradingStrategy(StrategyTemplate strategyTemplate) {
        super(strategyTemplate);
    }

    @Override
    public OrderCommand handleInstantInfo(List<Candle> info) {
        return null;
    }

    @Override
    public void initializeStrategy(InitializationResponse initializeInfo) {
        log.info(String.format("Инициализация стратегии %s", name));
        log.trace(initializeInfo.toString());
    }

    @Override
    public InitializationRequest getInitializeInfo() {
        return InitializationRequest.builder()
                .accountId(accountId)
                .strategyName(name)
                .instrumentsInfo(instruments.stream()
                        .map(i -> InstrumentInfoRequest.builder()
                                .api(api)
                                .name(i)
                                .interval(CandleInterval.ONE_HOUR)
                                .duration(Duration.ofDays(2))
                                .build())
                        .toList())
                .build();
    }

    @Override
    public boolean checkType(StrategyType strategyType) {
        return StrategyType.SIMPLE_STRATEGY.equals(strategyType);
    }

    @Override
    protected Class<? extends Strategy> getLoggingClass() {
        return SimpleTradingStrategy.class;
    }
}
