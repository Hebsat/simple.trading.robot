package com.simple.trading.robot.service.api.tinkoff.mapper;

import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.service.api.ApiType;
import com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tinkoff.piapi.core.models.Portfolio;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {TinkoffConverterUtil.class})
public interface AccountInfoMapper {

    @Mapping(target = "currency", source = "portfolio.totalAmountCurrencies.currency", qualifiedByName = "toCurrency")
    @Mapping(target = "amount", source = "portfolio.totalAmountCurrencies.value")
    AccountInfo mapToAccountInfo(String accountId, Portfolio portfolio, boolean isSandbox, ApiType apiType);
}
