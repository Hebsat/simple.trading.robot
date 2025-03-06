package com.simple.trading.robot.service.api.tinkoff.mapper;

import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {TinkoffConverterUtil.class})
public interface CandleMapper {

    @Mapping(target = "instrument", source = "request.instrument")
    @Mapping(target = "openingPrice", source = "historicCandle.open", qualifiedByName = "toBigDecimal")
    @Mapping(target = "closingPrice", source = "historicCandle.close", qualifiedByName = "toBigDecimal")
    @Mapping(target = "highestPrice", source = "historicCandle.high", qualifiedByName = "toBigDecimal")
    @Mapping(target = "lowestPrice", source = "historicCandle.low", qualifiedByName = "toBigDecimal")
    @Mapping(target = "openingTime", source = "historicCandle.time", qualifiedByName = "timestampToTime")
    @Mapping(target = "interval", source = "request.interval")
    Candle mapToCandle(InstrumentInfoRequest request, HistoricCandle historicCandle);
}
