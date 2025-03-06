package com.simple.trading.robot.service.impl;

import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.service.api.ApiSelector;
import com.simple.trading.robot.service.api.MarketApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandleServiceImplTest {

    @InjectMocks
    private CandleServiceImpl candleService;

    @Mock
    private ApiSelector apiSelector;
    @Mock
    private MarketApi marketApi;

    @Test
    void getInstrumentCandles() {
        Candle candle = Candle.builder().build();
        when(apiSelector.getApiByType(any())).thenReturn(marketApi);
        when(marketApi.getInstrumentHistory(any())).thenReturn(List.of(candle));

        List<Candle> result = candleService.getInstrumentCandles(InstrumentInfoRequest.builder().build());

        verify(apiSelector).getApiByType(any());
        verify(marketApi).getInstrumentHistory(any());
        assertEquals(1, result.size());
        assertTrue(result.contains(candle));
    }
}
