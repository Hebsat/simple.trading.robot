package com.simple.trading.robot.service.market;

import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.exception.SimpleTradingRobotRuntimeException;
import com.simple.trading.robot.executor.MarketListenerExecutor;
import com.simple.trading.robot.service.TradingService;
import com.simple.trading.robot.service.api.ApiSelector;
import com.simple.trading.robot.service.api.ApiType;
import com.simple.trading.robot.service.api.MarketApi;
import com.simple.trading.robot.strategy.StrategyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketListenerImplTest {

    @InjectMocks
    private MarketListenerImpl marketListener;

    @Mock
    private TradingService tradingService;
    @Mock
    private StrategyService strategyService;
    @Mock
    private MarketListenerExecutor executor;
    @Mock
    private ApiSelector apiSelector;
    @Mock
    private MarketApi marketApi;

    @Captor
    private ArgumentCaptor<Runnable> captor;

    @Test
    void init() {
        when(strategyService.getAllInstrumentsByApi()).thenReturn(Arrays.stream(ApiType.values()).collect(Collectors.toMap(Function.identity(), v -> new HashSet<>())));
        when(apiSelector.getApiByType(any(ApiType.class))).thenReturn(marketApi);

        marketListener.init();

        verify(strategyService).getAllInstrumentsByApi();
        verify(executor, times(ApiType.values().length)).execute(captor.capture());

        captor.getValue().run();

        verify(apiSelector).getApiByType(any(ApiType.class));
    }

    @Test
    void listenToMarket() {
        marketListener.listenToMarket(marketApi, Set.of());

        verify(marketApi).listenInstruments(anySet(), any());
    }

    @Test
    void listenToMarket_throwsException() {
        Set<Instrument> instruments = Set.of();
        Mockito.doThrow(SimpleTradingRobotRuntimeException.class).when(marketApi).listenInstruments(anySet(), any());

        assertThrows(SimpleTradingRobotRuntimeException.class, () -> marketListener.listenToMarket(marketApi, instruments));

        verify(marketApi).listenInstruments(anySet(), any());
        verify(executor).execute(any());
    }
}
