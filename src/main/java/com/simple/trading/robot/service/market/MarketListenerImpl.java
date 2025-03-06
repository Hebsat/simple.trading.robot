package com.simple.trading.robot.service.market;

import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.exception.SimpleTradingRobotRuntimeException;
import com.simple.trading.robot.executor.MarketListenerExecutor;
import com.simple.trading.robot.service.TradingService;
import com.simple.trading.robot.service.api.ApiSelector;
import com.simple.trading.robot.service.api.MarketApi;
import com.simple.trading.robot.strategy.StrategyService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketListenerImpl implements MarketListener {

    private final TradingService tradingService;
    private final StrategyService strategyService;
    private final MarketListenerExecutor executor;
    private final ApiSelector apiSelector;

    @PostConstruct
    public void init() {
        strategyService.getAllInstrumentsByApi()
                .forEach((apiType, instruments) -> executor.execute(() -> listenToMarket(apiSelector.getApiByType(apiType), instruments)));
    }

    @Override
    public void listenToMarket(MarketApi api, Set<Instrument> instruments) {
        try {
            api.listenInstruments(instruments, tradingService::handleCurrentPrice);
        } catch (SimpleTradingRobotRuntimeException e) {
            log.error("Перезапуск MarketListener для инструментов {}", instruments);
            log.error(e.getLocalizedMessage());
            executor.execute(() -> this.listenToMarket(api, instruments));
            throw e;
        }
    }
}
