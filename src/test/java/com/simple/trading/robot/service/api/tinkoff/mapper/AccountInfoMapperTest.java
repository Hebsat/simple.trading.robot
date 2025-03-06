package com.simple.trading.robot.service.api.tinkoff.mapper;

import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.entity.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tinkoff.piapi.core.models.Money;
import ru.tinkoff.piapi.core.models.Portfolio;

import java.math.BigDecimal;

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
        AccountInfo accountInfo = accountInfoMapper.mapToAccountInfo(portfolio);

        assertEquals(Currency.RUB, accountInfo.getCurrency());
        assertEquals(portfolio.getTotalAmountCurrencies().getValue(), accountInfo.getAmount());
    }

    @Test
    void mapToAccountInfo_returnNull() {
        AccountInfo accountInfo = accountInfoMapper.mapToAccountInfo(null);

        assertNull(accountInfo);
    }
}
