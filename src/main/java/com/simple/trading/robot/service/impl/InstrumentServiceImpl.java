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
import com.simple.trading.robot.service.InstrumentService;
import com.simple.trading.robot.service.api.ApiSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.simple.trading.robot.entity.InstrumentType.BOND;
import static com.simple.trading.robot.entity.InstrumentType.CURRENCY;
import static com.simple.trading.robot.entity.InstrumentType.ETF;
import static com.simple.trading.robot.entity.InstrumentType.FUTURE;
import static com.simple.trading.robot.entity.InstrumentType.SHARE;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstrumentServiceImpl implements InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final ApiSelector apiSelector;

    private static final Map<InstrumentType, Supplier<Instrument>> INSTRUMENT_FACTORY = Map.of(
            SHARE, Share::new,
            BOND, Bond::new,
            FUTURE, Future::new,
            ETF, Etf::new,
            CURRENCY, Currency::new
    );

    @Override
    public void updateInstrument(InstrumentInfoRequest instrumentInfo) {
        Optional<Instrument> optionalInstrument = instrumentRepository.getInstrumentByTicker(instrumentInfo.getInstrument().getTicker());
        if (optionalInstrument.isPresent()) {
            instrumentInfo.setInstrument(optionalInstrument.get());
            return;
        }
        Instrument instrument = INSTRUMENT_FACTORY.get(instrumentInfo.getInstrument().getType()).get();
        instrument.setTicker(instrumentInfo.getInstrument().getTicker());
        apiSelector.getApiByType(instrumentInfo.getApi()).updateInstrumentInfo(instrument);
        instrumentInfo.setInstrument(instrumentRepository.save(instrument));
    }
}
