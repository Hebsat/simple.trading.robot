package com.simple.trading.robot.strategy;

import com.simple.trading.robot.dto.properties.AccountTemplate;
import com.simple.trading.robot.dto.properties.InstrumentTemplate;
import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.dto.strategy.StrategyType;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.service.CandleService;
import com.simple.trading.robot.service.InstrumentService;
import com.simple.trading.robot.service.OrderService;
import com.simple.trading.robot.service.api.ApiType;
import com.simple.trading.robot.strategy.strategies.SimpleTradingStrategy;
import com.simple.trading.robot.strategy.strategies.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StrategyServiceImplTest {

    @InjectMocks
    private StrategyServiceImpl strategyService;

    @Mock
    private StrategyPrepareService strategyPrepareService;
    @Mock
    private OrderService orderService;
    @Mock
    private CandleService candleService;
    @Mock
    private InstrumentService instrumentService;

    private final String strategyName1 = "strategy1";
    private final String strategyName2 = "strategy2";
    private final AccountTemplate accountTemplate = AccountTemplate.builder()
            .id("123")
            .api(ApiType.TINKOFF_API)
            .build();
    private final InstrumentTemplate instrumentTemplate = InstrumentTemplate.builder()
            .ticker("TICKER")
            .build();
    private final StrategyTemplate strategyTemplate1 = StrategyTemplate.builder()
            .account(accountTemplate)
            .name(strategyName1)
            .strategyType(StrategyType.SIMPLE_STRATEGY)
            .instrument(instrumentTemplate)
            .build();
    private final StrategyTemplate strategyTemplate2 = StrategyTemplate.builder()
            .account(accountTemplate)
            .name(strategyName2)
            .strategyType(StrategyType.SIMPLE_STRATEGY)
            .instrument(instrumentTemplate)
            .build();
    private final Strategy strategy1 = new SimpleTradingStrategy(strategyTemplate1);
    private final Strategy strategy2 = new SimpleTradingStrategy(strategyTemplate2);

    @BeforeEach
    void setUp() {
    }

    @Test
    void getStrategy() {
        ReflectionTestUtils.setField(strategyService, "allStrategies", Map.of(strategy1.getName(), strategy1));

        Strategy result = strategyService.getStrategy(strategy1.getName());

        assertNotNull(result);
        assertEquals(strategy1, result);
    }

    @Test
    void getStrategiesByInstrument_success() {
        ReflectionTestUtils.setField(strategyService, "strategiesByInstrument", Map.of(strategy1.getInstrument(), List.of(strategy1)));

        List<Strategy> result = strategyService.getStrategiesByInstrument(strategy1.getInstrument());

        assertEquals(1, result.size());
        assertTrue(result.contains(strategy1));
    }

    @Test
    void getStrategiesByInstrument_empty() {
        List<Strategy> result = strategyService.getStrategiesByInstrument(strategy1.getInstrument());

        assertTrue(result.isEmpty());
    }

    @Test
    void init() {
        when(strategyPrepareService.getStrategies()).thenReturn(List.of(strategyTemplate1, strategyTemplate2));
        when(orderService.getOpenedOrdersByInstrumentAndAccountAndStrategyName(any(), anyString(), anyString())).thenReturn(Collections.emptyList());
        when(candleService.getInstrumentCandles(any())).thenReturn(Collections.emptyList());

        strategyService.init();

        assertEquals(2, strategyService.getAllStrategies().size());
        assertTrue(strategyService.getAllStrategies().containsKey(strategyName1));
        assertTrue(strategyService.getAllStrategies().containsKey(strategyName2));
        assertEquals(strategyName1, strategyService.getAllStrategies().get(strategyName1).getName());
        assertEquals(strategyName2, strategyService.getAllStrategies().get(strategyName2).getName());

        assertEquals(2, strategyService.getAllInstrumentsByApi().get(ApiType.TINKOFF_API).size());
        assertTrue(strategyService.getAllInstrumentsByApi().get(ApiType.TINKOFF_API).stream().allMatch(i -> i.getTicker().equals(instrumentTemplate.getTicker())));

        assertEquals(1, strategyService.getStrategiesByInstrument(strategyService.getAllStrategies().get(strategyName1).getInstrument()).size());
        assertEquals(1, strategyService.getStrategiesByInstrument(strategyService.getAllStrategies().get(strategyName2).getInstrument()).size());
    }

    @Test
    void getAllStrategies() {
        ReflectionTestUtils.setField(strategyService, "allStrategies", Map.of(strategy1.getName(), strategy1, strategy2.getName(), strategy2));

        Map<String, Strategy> result = strategyService.getAllStrategies();

        assertEquals(2, result.size());
        assertTrue(result.containsKey(strategy1.getName()));
        assertTrue(result.containsKey(strategy2.getName()));
        assertTrue(result.containsValue(strategy1));
        assertTrue(result.containsValue(strategy2));
    }

    @Test
    void getAllInstrumentsByApi() {
        Instrument instrument = new Instrument();
        ReflectionTestUtils.setField(strategyService, "allInstrumentsByApi", Map.of(ApiType.TINKOFF_API, Set.of(instrument)));

        Map<ApiType, Set<Instrument>> result = strategyService.getAllInstrumentsByApi();

        assertTrue(result.containsKey(ApiType.TINKOFF_API));
        assertTrue(result.get(ApiType.TINKOFF_API).contains(instrument));
    }
}
