package com.simple.trading.robot.service.api.tinkoff.mapper;

import com.google.protobuf.Timestamp;
import com.simple.trading.robot.dto.CandleInterval;
import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = {CandleMapperImpl.class})
class CandleMapperTest {

    @Autowired
    private CandleMapper candleMapper;

    private final OffsetDateTime time = OffsetDateTime.now();
    private final Instrument instrument = new Instrument();
    private final Quotation low = Quotation.newBuilder()
            .setUnits(11)
            .setNano(1)
            .build();
    private final Quotation open = Quotation.newBuilder()
            .setUnits(12)
            .setNano(2)
            .build();
    private final Quotation close = Quotation.newBuilder()
            .setUnits(13)
            .setNano(3)
            .build();
    private final Quotation high = Quotation.newBuilder()
            .setUnits(14)
            .setNano(4)
            .build();
    private final InstrumentInfoRequest instrumentInfoRequest = InstrumentInfoRequest.builder()
            .instrument(instrument)
            .interval(CandleInterval.ONE_MINUTE)
            .build();
    private final HistoricCandle historicCandle = HistoricCandle.newBuilder()
            .setOpen(open)
            .setClose(close)
            .setLow(low)
            .setHigh(high)
            .setTime(Timestamp.newBuilder().setSeconds(time.toEpochSecond()).setNanos(time.getNano()).build())
            .build();

    @Test
    void mapToCandle() {
        Candle candle = candleMapper.mapToCandle(instrumentInfoRequest, historicCandle);

        assertEquals(instrument, candle.getInstrument());
        assertEquals(TinkoffConverterUtil.toBigDecimal(open), candle.getOpeningPrice());
        assertEquals(TinkoffConverterUtil.toBigDecimal(close), candle.getClosingPrice());
        assertEquals(TinkoffConverterUtil.toBigDecimal(high), candle.getHighestPrice());
        assertEquals(TinkoffConverterUtil.toBigDecimal(low), candle.getLowestPrice());
        assertEquals(time, candle.getOpeningTime());
        assertEquals(instrumentInfoRequest.getInterval(), candle.getInterval());
    }

    @Test
    void mapToCandle_returnNull() {
        Candle candle = candleMapper.mapToCandle(null, null);

        assertNull(candle);
    }
}
