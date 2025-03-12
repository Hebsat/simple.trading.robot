package com.simple.trading.robot.strategy;

import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.dto.strategy.InitializationRequest;
import com.simple.trading.robot.dto.strategy.InitializationResponse;
import com.simple.trading.robot.dto.strategy.StrategyType;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.entity.Order;
import com.simple.trading.robot.service.CandleService;
import com.simple.trading.robot.service.InstrumentService;
import com.simple.trading.robot.service.OrderService;
import com.simple.trading.robot.service.api.ApiType;
import com.simple.trading.robot.strategy.strategies.AnotherTradingStrategy;
import com.simple.trading.robot.strategy.strategies.SimpleTradingStrategy;
import com.simple.trading.robot.strategy.strategies.Strategy;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.simple.trading.robot.dto.strategy.StrategyType.ANOTHER_STRATEGY;
import static com.simple.trading.robot.dto.strategy.StrategyType.SIMPLE_STRATEGY;

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyServiceImpl implements StrategyService {

    private final StrategyPrepareService strategyPrepareService;
    private final OrderService orderService;
    private final CandleService candleService;
    private final InstrumentService instrumentService;

    @Getter(onMethod_ = @Override)
    private Map<String, Strategy> allStrategies = new HashMap<>();
    @Getter(onMethod_ = @Override)
    private Map<ApiType, Set<Instrument>> allInstrumentsByApi = new EnumMap<>(ApiType.class);

    private final Map<Instrument, List<Strategy>> strategiesByInstrument = new HashMap<>();
    private static final Map<StrategyType, Function<StrategyTemplate, Strategy>> STRATEGY_FACTORY = new EnumMap<>(
            Map.of(
                    SIMPLE_STRATEGY, SimpleTradingStrategy::new,
                    ANOTHER_STRATEGY, AnotherTradingStrategy::new
            )
    );

    @Override
    public Strategy getStrategy(String name) {
        return allStrategies.get(name);
    }

    @Override
    public List<Strategy> getStrategiesByInstrument(Instrument instrument) {
        return strategiesByInstrument.containsKey(instrument) ?
                strategiesByInstrument.get(instrument) :
                new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        strategyPrepareService.getStrategies().forEach(this::createStrategy);
    }

    private void createStrategy(StrategyTemplate strategyTemplate) {
        log.debug("Создается стратегия {}", strategyTemplate.getName());
        Strategy strategy = STRATEGY_FACTORY.get(strategyTemplate.getStrategyType()).apply(strategyTemplate);
        strategy.initializeStrategy(prepareInitialization(strategy.getInitializeInfo()));
        allStrategies.put(strategy.getName(), strategy);
        strategiesByInstrument.computeIfAbsent(strategy.getInstrument(), k -> new ArrayList<>()).add(strategy);
        allInstrumentsByApi.computeIfAbsent(strategy.getApi(), k -> new HashSet<>()).add(strategy.getInstrument());
    }

    private InitializationResponse prepareInitialization(InitializationRequest initializationRequest) {
        instrumentService.updateInstrument(initializationRequest.getInstrumentInfo());
        List<Order> orders = orderService.getOpenedOrdersByInstrumentAndAccountAndStrategyName(
                initializationRequest.getInstrumentInfo().getInstrument(),
                        initializationRequest.getAccountId(),
                        initializationRequest.getStrategyName());
        List<Candle> instrumentCandles = candleService.getInstrumentCandles(initializationRequest.getInstrumentInfo());

        return InitializationResponse.builder()
                .instrument(initializationRequest.getInstrumentInfo().getInstrument())
                .instrumentCandles(instrumentCandles)
                .orders(orders)
                .build();
    }
}
