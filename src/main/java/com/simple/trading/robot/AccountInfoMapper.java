package com.simple.trading.robot;

import com.simple.trading.robot.dto.api.AccountInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tinkoff.piapi.core.models.Portfolio;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface AccountInfoMapper {

    @Mapping(target = "currency", source = "totalAmountCurrencies.currency")
    @Mapping(target = "amount", source = "totalAmountCurrencies.value")
    AccountInfo mapToAccountInfo(Portfolio portfolio);
}
