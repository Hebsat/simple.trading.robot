package com.simple.trading.robot.service.api.tinkoff.mapper;

import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tinkoff.piapi.core.models.Portfolio;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {TinkoffConverterUtil.class})
public interface AccountInfoMapper {

    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "currency", source = "totalAmountCurrencies.currency", qualifiedByName = "toCurrency")
    @Mapping(target = "amount", source = "totalAmountCurrencies.value")
    AccountInfo mapToAccountInfo(Portfolio portfolio);
}
