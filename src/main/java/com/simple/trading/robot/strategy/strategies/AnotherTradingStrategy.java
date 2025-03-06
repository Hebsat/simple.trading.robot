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

public class AnotherTradingStrategy extends AbstractStrategy {

    public AnotherTradingStrategy(StrategyTemplate strategyTemplate) {
        super(strategyTemplate);
    }

    @Override
    public OrderCommand handleInstantInfo(List<Candle> info) {
        return null;
    }

    @Override
    public void initializeStrategy(InitializationResponse initializeInfo) {
        log.info(String.format("Инициализация стратегии %s", name));
        instrument = initializeInfo.getInstrument();
        log.trace(initializeInfo.toString());
    }

    @Override
    public InitializationRequest getInitializeInfo() {
        return InitializationRequest.builder()
                .accountId(accountId)
                .strategyName(name)
                .instrumentInfo(
                        InstrumentInfoRequest.builder()
                                .api(api)
                                .instrument(instrument)
                                .interval(CandleInterval.ONE_MINUTE)
                                .duration(Duration.ofHours(5))
                                .build())
                .build();
    }

    @Override
    public boolean checkType(StrategyType strategyType) {
        return StrategyType.ANOTHER_STRATEGY.equals(strategyType);
    }

    @Override
    protected Class<? extends Strategy> getLoggingClass() {
        return AnotherTradingStrategy.class;
    }
}
