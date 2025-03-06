package com.simple.trading.robot.service.impl;

import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.entity.InstrumentType;
import com.simple.trading.robot.entity.instrument.Bond;
import com.simple.trading.robot.entity.instrument.Currency;
import com.simple.trading.robot.entity.instrument.Etf;
import com.simple.trading.robot.entity.instrument.Future;
import com.simple.trading.robot.entity.instrument.Share;
import com.simple.trading.robot.repository.InstrumentRepository;
import com.simple.trading.robot.service.api.ApiSelector;
import com.simple.trading.robot.service.api.MarketApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstrumentServiceImplTest {

    @InjectMocks
    private InstrumentServiceImpl instrumentService;

    @Mock
    private InstrumentRepository instrumentRepository;
    @Mock
    private ApiSelector apiSelector;
    @Mock
    private MarketApi marketApi;

    @Test
    void updateInstrument_foundInRepo() {
        Instrument oldInstrument = new Instrument();
        oldInstrument.setTicker("TICKER");
        InstrumentInfoRequest instrumentInfoRequest = InstrumentInfoRequest.builder().instrument(oldInstrument).build();
        Instrument newInstrument = new Instrument();
        when(instrumentRepository.getInstrumentByTicker(anyString())).thenReturn(Optional.of(newInstrument));

        instrumentService.updateInstrument(instrumentInfoRequest);

        assertEquals(newInstrument, instrumentInfoRequest.getInstrument());
    }

    @ParameterizedTest
    @MethodSource("getClasses")
    void updateInstrument_notfoundInRepo(InstrumentType type, Class<? extends Instrument> instrumentClass) {
        Instrument oldInstrument = new Instrument();
        oldInstrument.setTicker("TICKER");
        oldInstrument.setType(type);
        InstrumentInfoRequest instrumentInfoRequest = InstrumentInfoRequest.builder().instrument(oldInstrument).build();
        when(instrumentRepository.getInstrumentByTicker(anyString())).thenReturn(Optional.empty());
        when(apiSelector.getApiByType(any())).thenReturn(marketApi);
        when(instrumentRepository.save(Mockito.any(Instrument.class))).thenAnswer(i -> i.getArguments()[0]);

        instrumentService.updateInstrument(instrumentInfoRequest);

        verify(marketApi).updateInstrumentInfo(any());
        verify(instrumentRepository).save(any());
        assertNotEquals(oldInstrument, instrumentInfoRequest.getInstrument());
        assertEquals(oldInstrument.getTicker(), instrumentInfoRequest.getInstrument().getTicker());
        assertEquals(instrumentClass, instrumentInfoRequest.getInstrument().getClass());
    }

    private static Stream<Arguments> getClasses() {
        return Stream.of(
                Arguments.of(InstrumentType.BOND, Bond.class),
                Arguments.of(InstrumentType.SHARE, Share.class),
                Arguments.of(InstrumentType.FUTURE, Future.class),
                Arguments.of(InstrumentType.CURRENCY, Currency.class),
                Arguments.of(InstrumentType.ETF, Etf.class)
        );
    }
}
