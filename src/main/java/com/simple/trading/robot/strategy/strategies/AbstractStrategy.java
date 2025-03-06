package com.simple.trading.robot.strategy.strategies;

import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.exception.SimpleTradingRobotRuntimeException;
import com.simple.trading.robot.service.api.ApiType;
import lombok.Getter;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogAccessor;

import java.math.BigDecimal;

public abstract class AbstractStrategy implements Strategy {

    protected final LogAccessor log = new LogAccessor(LogFactory.getLog(getLoggingClass()));

    @Getter(onMethod_ = @Override)
    protected String name;
    @Getter(onMethod_ = @Override)
    protected ApiType api;
    protected String accountId;
    private boolean isSandbox;
    private BigDecimal amount;
    @Getter(onMethod_ = @Override)
    protected Instrument instrument;

    protected AbstractStrategy(StrategyTemplate strategyTemplate) {
        if (!checkType(strategyTemplate.getStrategyType())) {
            throw new SimpleTradingRobotRuntimeException(String.format("Невозможно проинициализировать стратегию %s из-за несоответствия типа", strategyTemplate.getName()));
        }
        this.name = strategyTemplate.getName();
        this.api = strategyTemplate.getAccount().getApi();
        this.accountId = strategyTemplate.getAccount().getId();
        this.amount = strategyTemplate.getAccount().getMaxSum();
        this.isSandbox = strategyTemplate.getAccount().isSandbox();
        this.instrument = new Instrument();
        this.instrument.setTicker(strategyTemplate.getInstrument().getTicker());
        this.instrument.setType(strategyTemplate.getInstrument().getType());
    }

    abstract Class<? extends Strategy> getLoggingClass();
}
