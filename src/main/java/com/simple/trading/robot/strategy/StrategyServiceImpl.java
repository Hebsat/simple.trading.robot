package com.simple.trading.robot.strategy;

import com.simple.trading.robot.dto.strategy.InitializationRequest;
import com.simple.trading.robot.dto.strategy.InitializationResponse;
import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.entity.Candle;
import com.simple.trading.robot.entity.Order;
import com.simple.trading.robot.service.CandleService;
import com.simple.trading.robot.service.OrderService;
import com.simple.trading.robot.strategy.factory.StrategyFactory;
import com.simple.trading.robot.strategy.strategies.Strategy;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyServiceImpl implements StrategyService {

    private final StrategyFactory strategyFactory;
    private final StrategyPrepareService strategyPrepareService;
    private final OrderService orderService;
    private final CandleService candleService;

    @Getter(onMethod_ = @Override)
    private Map<String, Strategy> allStrategies = new HashMap<>();
    @Getter(onMethod_ = @Override)
    private Map<String, List<String>> strategiesInstruments = new HashMap<>();
    @Getter(onMethod_ = @Override)
    private Set<String> allInstruments = new HashSet<>();

    @Override
    public Strategy getStrategy(String name) {
        return allStrategies.get(name);
    }

    @PostConstruct
    public void init() {
        strategyPrepareService.getStrategies().forEach(this::createStrategy);
        strategiesInstruments.values().forEach(allInstruments::addAll);

    }

    private void createStrategy(StrategyTemplate strategyTemplate) {
        Strategy strategy = strategyFactory.createStrategy(strategyTemplate);
        strategy.initializeStrategy(prepareInitialization(strategy.getInitializeInfo()));
        allStrategies.put(strategy.getName(), strategy);
        strategiesInstruments.put(strategy.getName(), strategy.getInstruments());
    }

    public InitializationResponse prepareInitialization(InitializationRequest initializationRequest) {
        List<Order> orders = initializationRequest.getInstrumentsInfo().stream()
                .map(InstrumentInfoRequest::getName)
                .map(i -> orderService.getOpenedOrdersByInstrumentAndAccount(i, initializationRequest.getAccountId()))
                .flatMap(Collection::stream)
                .toList();
        Map<InstrumentInfoRequest, List<Candle>> instrumentCandles = initializationRequest.getInstrumentsInfo().stream()
                .collect(Collectors.toMap(Function.identity(), candleService::getInstrumentCandles));

        return InitializationResponse.builder()
                .instrumentCandles(instrumentCandles)
                .orders(orders)
                .build();
    }
}
