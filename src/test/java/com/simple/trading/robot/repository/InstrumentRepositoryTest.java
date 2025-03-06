package com.simple.trading.robot.repository;

import com.simple.trading.robot.entity.Currency;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.entity.instrument.Etf;
import com.simple.trading.robot.entity.instrument.Share;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InstrumentRepositoryTest {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Test
    void getInstrumentByTicker() {
        Instrument instrument1 = new Share();
        instrument1.setCurrency(Currency.RUB);
        instrument1.setTicker("ABC");
        Instrument instrument2 = new Etf();
        instrument2.setCurrency(Currency.RUB);
        instrument2 .setTicker("XYZ");
        instrumentRepository.saveAll(List.of(instrument1, instrument2));

        Optional<Instrument> result = instrumentRepository.getInstrumentByTicker("ABC");

        assertTrue(result.isPresent());
        assertEquals(instrument1, result.get());
    }
}
