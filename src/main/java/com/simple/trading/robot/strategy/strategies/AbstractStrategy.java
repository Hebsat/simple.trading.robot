package com.simple.trading.robot.strategy.strategies;

import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.exception.SimpleTradingRobotRuntimeException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStrategy implements Strategy {

    @Getter(onMethod_ = @Override)
    private String name;
    protected String accountId;
    private boolean isSandbox;
    private BigDecimal amount;
    @Getter(onMethod_ = @Override)
    protected List<String> instruments;

    protected AbstractStrategy(StrategyTemplate strategyTemplate) {
        if (!checkType(strategyTemplate.getStrategyType())) {
            throw new SimpleTradingRobotRuntimeException(String.format("Невозможно проинициализировать стратегию %s из-за несоответствия типа", strategyTemplate.getName()));
        }
        this.name = strategyTemplate.getName();
        this.accountId = strategyTemplate.getAccount().getId();
        this.amount = strategyTemplate.getAccount().getMaxSum();
        this.instruments = new ArrayList<>(List.of(strategyTemplate.getInstrument()));
        this.isSandbox = strategyTemplate.getAccount().isSandbox();
    }
}
