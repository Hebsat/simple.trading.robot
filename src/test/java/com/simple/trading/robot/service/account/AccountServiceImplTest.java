package com.simple.trading.robot.service.account;

import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.dto.properties.AccountTemplate;
import com.simple.trading.robot.dto.properties.InstrumentTemplate;
import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.dto.strategy.StrategyType;
import com.simple.trading.robot.entity.Currency;
import com.simple.trading.robot.entity.InstrumentType;
import com.simple.trading.robot.exception.SimpleTradingRobotPropertiesException;
import com.simple.trading.robot.service.api.ApiSelector;
import com.simple.trading.robot.service.api.ApiType;
import com.simple.trading.robot.service.api.MarketApi;
import com.simple.trading.robot.strategy.StrategyPrepareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private ApiSelector apiSelector;
    @Mock
    private StrategyPrepareService strategyPrepareService;
    @Mock
    private MarketApi marketApi;

    private StrategyTemplate strategyTemplate1;
    private StrategyTemplate strategyTemplate2;
    private AccountTemplate accountTemplate1;
    private AccountTemplate accountTemplate2;

    @BeforeEach
    void setUp() {
        InstrumentTemplate instrumentTemplate1 = InstrumentTemplate.builder()
                .ticker("ABC")
                .type(InstrumentType.SHARE)
                .build();
        accountTemplate1 = AccountTemplate.builder()
                .api(ApiType.TINKOFF_API)
                .id("123")
                .maxSum(BigDecimal.TEN)
                .maxPercent(BigDecimal.valueOf(40))
                .build();
        accountTemplate2 = AccountTemplate.builder()
                .api(ApiType.TINKOFF_API)
                .id("123")
                .maxSum(BigDecimal.ZERO)
                .maxPercent(BigDecimal.valueOf(50))
                .build();
        strategyTemplate1 =StrategyTemplate.builder()
                .enabled(true)
                .name("strategy1")
                .strategyType(StrategyType.SIMPLE_STRATEGY)
                .instrument(instrumentTemplate1)
                .account(accountTemplate1)
                .build();
        strategyTemplate2 =StrategyTemplate.builder()
                .enabled(true)
                .name("strategy2")
                .strategyType(StrategyType.SIMPLE_STRATEGY)
                .instrument(instrumentTemplate1)
                .account(accountTemplate2)
                .build();
    }

    @Test
    void getAccountInfo() {
        when(apiSelector.getApiByType(any(ApiType.class))).thenReturn(marketApi);

        accountService.getAccountInfo(ApiType.TINKOFF_API, true, "123");

        verify(apiSelector).getApiByType(any(ApiType.class));
        verify(marketApi).getAccountInfo(anyString(), anyBoolean());
    }

    @Test
    void init_openNewSandboxAccount() {
        accountTemplate1.setId("open");
        accountTemplate1.setSandbox(true);
        when(strategyPrepareService.getStrategiesByApiByAccounts()).thenReturn(Map.of(accountTemplate1.getApi(), Map.of(accountTemplate1.getId(), List.of(strategyTemplate1))));
        when(apiSelector.getApiByType(any())).thenReturn(marketApi);
        when(marketApi.openSandboxAccount()).thenReturn("");

        assertThrows(SimpleTradingRobotPropertiesException.class, () -> accountService.init());
    }

    @Test
    void init_notFoundAccountInSandbox() {
        accountTemplate1.setSandbox(true);
        when(strategyPrepareService.getStrategiesByApiByAccounts()).thenReturn(Map.of(accountTemplate1.getApi(), Map.of(accountTemplate1.getId(), List.of(strategyTemplate1))));
        when(apiSelector.getApiByType(any())).thenReturn(marketApi);
        when(marketApi.getAccountIds(anyBoolean())).thenReturn(Set.of());

        assertThrows(SimpleTradingRobotPropertiesException.class, () -> accountService.init());
    }

    @Test
    void init_notFoundRealAccount() {
        when(strategyPrepareService.getStrategiesByApiByAccounts()).thenReturn(Map.of(accountTemplate1.getApi(), Map.of(accountTemplate1.getId(), List.of(strategyTemplate1))));
        when(apiSelector.getApiByType(any())).thenReturn(marketApi);
        when(marketApi.getAccountIds(anyBoolean())).thenReturn(Set.of());

        assertThrows(SimpleTradingRobotPropertiesException.class, () -> accountService.init());
    }

    @Test
    void init_maxPercentIsOver100() {
        accountTemplate1.setSandbox(true);
        accountTemplate2.setSandbox(true);
        accountTemplate2.setMaxPercent(BigDecimal.valueOf(70));
        when(strategyPrepareService.getStrategiesByApiByAccounts()).thenReturn(Map.of(accountTemplate1.getApi(), Map.of(accountTemplate1.getId(), List.of(strategyTemplate1, strategyTemplate2))));
        when(apiSelector.getApiByType(any())).thenReturn(marketApi);
        when(marketApi.getAccountIds(anyBoolean())).thenReturn(Set.of(accountTemplate1.getId()));

        assertThrows(SimpleTradingRobotPropertiesException.class, () -> accountService.init());
    }

    @Test
    void init_totalSumIsOverThanOnAccount() {
        AccountInfo accountInfo = AccountInfo.builder().accountId(accountTemplate1.getId()).currency(Currency.RUB).amount(BigDecimal.TEN).build();
        when(strategyPrepareService.getStrategiesByApiByAccounts()).thenReturn(Map.of(accountTemplate1.getApi(), Map.of(accountTemplate1.getId(), List.of(strategyTemplate1, strategyTemplate2))));
        when(apiSelector.getApiByType(any())).thenReturn(marketApi);
        when(marketApi.getAccountIds(anyBoolean())).thenReturn(Set.of(accountTemplate1.getId()));
        when(marketApi.getAccountInfo(anyString(), anyBoolean())).thenReturn(accountInfo);

        accountService.init();

        assertEquals(BigDecimal.valueOf(4), accountTemplate1.getMaxSum());
        assertEquals(BigDecimal.valueOf(5), accountTemplate2.getMaxSum());
    }

    @Test
    void init_totalSumIsLowerThanPercentage() {
        accountTemplate1.setMaxSum(BigDecimal.ONE);
        accountTemplate2.setMaxSum(BigDecimal.TWO);
        AccountInfo accountInfo = AccountInfo.builder().accountId(accountTemplate1.getId()).currency(Currency.RUB).amount(BigDecimal.TEN).build();
        when(strategyPrepareService.getStrategiesByApiByAccounts()).thenReturn(Map.of(accountTemplate1.getApi(), Map.of(accountTemplate1.getId(), List.of(strategyTemplate1, strategyTemplate2))));
        when(apiSelector.getApiByType(any())).thenReturn(marketApi);
        when(marketApi.getAccountIds(anyBoolean())).thenReturn(Set.of(accountTemplate1.getId()));
        when(marketApi.getAccountInfo(anyString(), anyBoolean())).thenReturn(accountInfo);

        accountService.init();

        assertEquals(BigDecimal.ONE, accountTemplate1.getMaxSum());
        assertEquals(BigDecimal.TWO, accountTemplate2.getMaxSum());
    }
}
