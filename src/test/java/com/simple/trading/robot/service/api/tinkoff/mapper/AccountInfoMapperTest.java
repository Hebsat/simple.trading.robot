package com.simple.trading.robot.service.api.tinkoff.mapper;

import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.entity.Currency;
import com.simple.trading.robot.service.api.ApiType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tinkoff.piapi.core.models.Money;
import ru.tinkoff.piapi.core.models.Portfolio;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = {AccountInfoMapperImpl.class})
class AccountInfoMapperTest {

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    private final Portfolio portfolio = Portfolio.builder()
            .totalAmountCurrencies(Money.builder()
                    .currency("rub")
                    .value(BigDecimal.TEN)
                    .build())
            .build();

    @Test
    void mapToAccountInfo() {
        String accountId = "accountId";
        boolean isSandbox = new Random().nextBoolean();
        ApiType apiType = ApiType.TINKOFF_API;
        AccountInfo accountInfo = accountInfoMapper.mapToAccountInfo(accountId, portfolio, isSandbox, apiType);

        assertEquals(accountId, accountInfo.getAccountId());
        assertEquals(isSandbox, accountInfo.isSandbox());
        assertEquals(apiType, accountInfo.getApiType());
        assertEquals(Currency.RUB, accountInfo.getCurrency());
        assertEquals(portfolio.getTotalAmountCurrencies().getValue(), accountInfo.getAmount());
    }

    @Test
    void mapToAccountInfo_returnNull() {
        AccountInfo accountInfo = accountInfoMapper.mapToAccountInfo(null,null,true,null);

        assertNull(accountInfo);
    }
}
