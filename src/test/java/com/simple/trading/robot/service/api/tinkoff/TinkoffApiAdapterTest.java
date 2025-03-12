package com.simple.trading.robot.service.api.tinkoff;

import com.simple.trading.robot.dto.CandleInterval;
import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.entity.instrument.Bond;
import com.simple.trading.robot.entity.instrument.Currency;
import com.simple.trading.robot.entity.instrument.Etf;
import com.simple.trading.robot.entity.instrument.Future;
import com.simple.trading.robot.entity.instrument.Share;
import com.simple.trading.robot.exception.SimpleTradingRobotRuntimeException;
import com.simple.trading.robot.service.api.ApiType;
import com.simple.trading.robot.service.api.tinkoff.mapper.AccountInfoMapper;
import com.simple.trading.robot.service.api.tinkoff.mapper.CandleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.tinkoff.piapi.core.OperationsService;
import ru.tinkoff.piapi.core.SandboxService;
import ru.tinkoff.piapi.core.UsersService;
import ru.tinkoff.piapi.core.models.Portfolio;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;
import ru.tinkoff.piapi.core.stream.MarketDataSubscriptionService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TinkoffApiAdapterTest {

    @InjectMocks
    private TinkoffApiAdapter tinkoffApiAdapter;

    @Mock
    private InvestApi investApi;
    @Mock
    private OperationsService operationsService;
    @Mock
    private SandboxService sandboxService;
    @Mock
    private UsersService usersService;
    @Mock
    private MarketDataService marketDataService;
    @Mock
    private MarketDataStreamService marketDataStreamService;
    @Mock
    private MarketDataSubscriptionService marketDataSubscriptionService;
    @Mock
    private InstrumentsService instrumentsService;
    @Mock
    private AccountInfoMapper accountInfoMapper;
    @Mock
    private CandleMapper candleMapper;
    @Mock
    private Consumer<Candle> consumer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tinkoffApiAdapter, "tinkoffApi", investApi);
        ReflectionTestUtils.setField(tinkoffApiAdapter, "sandboxApi", investApi);
    }

    @Test
    void init() {
        ReflectionTestUtils.setField(tinkoffApiAdapter, "tinkoffApi", null);
        ReflectionTestUtils.setField(tinkoffApiAdapter, "sandboxApi", null);
        tinkoffApiAdapter.init();

        InvestApi tinkoffApi = (InvestApi) ReflectionTestUtils.getField(tinkoffApiAdapter, "tinkoffApi");
        InvestApi sandboxApi = (InvestApi) ReflectionTestUtils.getField(tinkoffApiAdapter, "sandboxApi");

        assertNotNull(tinkoffApi);
        assertNotNull(sandboxApi);
    }

    @Test
    void getApiType() {
        ApiType apiType = tinkoffApiAdapter.getApiType();

        assertEquals(ApiType.TINKOFF_API, apiType);
    }

    @Test
    void getAccountInfo() {
        AccountInfo accountInfo = AccountInfo.builder().build();
        when(investApi.getOperationsService()).thenReturn(operationsService);
        when(operationsService.getPortfolioSync(anyString())).thenReturn(Portfolio.builder().build());
        when(accountInfoMapper.mapToAccountInfo(anyString(), any(Portfolio.class), anyBoolean(), any(ApiType.class))).thenReturn(accountInfo);

        AccountInfo result = tinkoffApiAdapter.getAccountInfo("accountId", true);

        assertEquals(accountInfo, result);
    }

    @Test
    void getAccountIds_forSandbox() {
        String accountId = UUID.randomUUID().toString();
        Account account = Account.newBuilder().setId(accountId).build();
        when(investApi.getSandboxService()).thenReturn(sandboxService);
        when(sandboxService.getAccountsSync()).thenReturn(List.of(account));

        Set<String> result = tinkoffApiAdapter.getAccountIds(true);

        assertEquals(1, result.size());
        assertTrue(result.contains(accountId));
    }

    @Test
    void getAccountIds_forTinkoffApi() {
        String accountId = UUID.randomUUID().toString();
        Account account = Account.newBuilder().setId(accountId).build();
        when(investApi.getUserService()).thenReturn(usersService);
        when(usersService.getAccountsSync()).thenReturn(List.of(account));

        Set<String> result = tinkoffApiAdapter.getAccountIds(false);

        assertEquals(1, result.size());
        assertTrue(result.contains(accountId));
    }

    @Test
    void openSandboxAccount() {
        String accountId = UUID.randomUUID().toString();
        when(investApi.getSandboxService()).thenReturn(sandboxService);
        when(sandboxService.openAccountSync()).thenReturn(accountId);

        String result = tinkoffApiAdapter.openSandboxAccount();

        assertEquals(accountId, result);
    }

    @Test
    void fillUpSandboxAccount_success() {
        when(investApi.getSandboxService()).thenReturn(sandboxService);
        when(sandboxService.payInSync(anyString(), any(MoneyValue.class))).thenReturn(MoneyValue.newBuilder().setUnits(1).build());

        boolean result = tinkoffApiAdapter.fillUpSandboxAccount("accountId", BigDecimal.ONE, com.simple.trading.robot.entity.Currency.RUB);

        assertTrue(result);
    }

    @Test
    void fillUpSandboxAccount_fail() {
        when(investApi.getSandboxService()).thenReturn(sandboxService);
        when(sandboxService.payInSync(anyString(), any(MoneyValue.class))).thenReturn(MoneyValue.newBuilder().setUnits(10).build());

        boolean result = tinkoffApiAdapter.fillUpSandboxAccount("accountId", BigDecimal.ONE, com.simple.trading.robot.entity.Currency.RUB);

        assertFalse(result);
    }

    @Test
    void getInstrumentHistory() {
        Candle candle = Candle.builder().build();
        Instrument instrument = new Instrument();
        instrument.setTinkoffId("TinkoffId");
        InstrumentInfoRequest request = InstrumentInfoRequest.builder().interval(CandleInterval.ONE_HOUR).duration(Duration.ZERO).instrument(instrument).build();
        when(investApi.getMarketDataService()).thenReturn(marketDataService);
        when(marketDataService.getCandlesSync(anyString(), any(Instant.class), any(Instant.class), any(ru.tinkoff.piapi.contract.v1.CandleInterval.class)))
                .thenReturn(List.of(HistoricCandle.newBuilder().build()));
        when(candleMapper.mapToCandle(any(InstrumentInfoRequest.class), any(HistoricCandle.class))).thenReturn(candle);

        List<Candle> result = tinkoffApiAdapter.getInstrumentHistory(request);

        assertEquals(1, result.size());
        assertTrue(result.contains(candle));
    }

    @Test
    void listenInstruments() {
        Instrument instrument = new Instrument();
        instrument.setTinkoffId(UUID.randomUUID().toString());
        when(investApi.getMarketDataStreamService()).thenReturn(marketDataStreamService);
        when(marketDataStreamService.newStream(any(), any(), any())).thenReturn(marketDataSubscriptionService);

        tinkoffApiAdapter.listenInstruments(List.of(instrument), consumer);

        verify(marketDataSubscriptionService).subscribeCandles(any());
    }

    @Test
    void updateInstrumentInfo_Bond() {
        String ticker = "TICKER";
        Bond bond = new Bond();
        bond.setTicker(ticker);
        when(investApi.getInstrumentsService()).thenReturn(instrumentsService);
        when(instrumentsService.getAllBondsSync()).thenReturn(List.of(ru.tinkoff.piapi.contract.v1.Bond.newBuilder().setTicker(ticker).build()));

        tinkoffApiAdapter.updateInstrumentInfo(bond);

        verify(instrumentsService).getAllBondsSync();
    }

    @Test
    void updateInstrumentInfo_BondThrows() {
        Bond bond = new Bond();
        when(investApi.getInstrumentsService()).thenReturn(instrumentsService);
        when(instrumentsService.getAllBondsSync()).thenReturn(List.of(ru.tinkoff.piapi.contract.v1.Bond.newBuilder().build()));

        assertThrows(SimpleTradingRobotRuntimeException.class, () -> tinkoffApiAdapter.updateInstrumentInfo(bond));
    }

    @Test
    void updateInstrumentInfo_Share() {
        String ticker = "TICKER";
        Share share = new Share();
        share.setTicker(ticker);
        when(investApi.getInstrumentsService()).thenReturn(instrumentsService);
        when(instrumentsService.getAllSharesSync()).thenReturn(List.of(ru.tinkoff.piapi.contract.v1.Share.newBuilder().setTicker(ticker).build()));

        tinkoffApiAdapter.updateInstrumentInfo(share);

        verify(instrumentsService).getAllSharesSync();
    }

    @Test
    void updateInstrumentInfo_ShareThrows() {
        Share share = new Share();
        when(investApi.getInstrumentsService()).thenReturn(instrumentsService);
        when(instrumentsService.getAllSharesSync()).thenReturn(List.of(ru.tinkoff.piapi.contract.v1.Share.newBuilder().build()));

        assertThrows(SimpleTradingRobotRuntimeException.class, () -> tinkoffApiAdapter.updateInstrumentInfo(share));
    }

    @Test
    void updateInstrumentInfo_Future() {
        String ticker = "TICKER";
        Future future = new Future();
        future.setTicker(ticker);
        when(investApi.getInstrumentsService()).thenReturn(instrumentsService);
        when(instrumentsService.getAllFuturesSync()).thenReturn(List.of(ru.tinkoff.piapi.contract.v1.Future.newBuilder().setTicker(ticker).build()));

        tinkoffApiAdapter.updateInstrumentInfo(future);

        verify(instrumentsService).getAllFuturesSync();
    }

    @Test
    void updateInstrumentInfo_FutureThrows() {
        Future future = new Future();
        when(investApi.getInstrumentsService()).thenReturn(instrumentsService);
        when(instrumentsService.getAllFuturesSync()).thenReturn(List.of(ru.tinkoff.piapi.contract.v1.Future.newBuilder().build()));

        assertThrows(SimpleTradingRobotRuntimeException.class, () -> tinkoffApiAdapter.updateInstrumentInfo(future));
    }

    @Test
    void updateInstrumentInfo_Currency() {
        String ticker = "TICKER";
        Currency currency = new Currency();
        currency.setTicker(ticker);
        when(investApi.getInstrumentsService()).thenReturn(instrumentsService);
        when(instrumentsService.getAllCurrenciesSync()).thenReturn(List.of(ru.tinkoff.piapi.contract.v1.Currency.newBuilder().setTicker(ticker).build()));

        tinkoffApiAdapter.updateInstrumentInfo(currency);

        verify(instrumentsService).getAllCurrenciesSync();
    }

    @Test
    void updateInstrumentInfo_CurrencyThrows() {
        Currency currency = new Currency();
        when(investApi.getInstrumentsService()).thenReturn(instrumentsService);
        when(instrumentsService.getAllCurrenciesSync()).thenReturn(List.of(ru.tinkoff.piapi.contract.v1.Currency.newBuilder().build()));

        assertThrows(SimpleTradingRobotRuntimeException.class, () -> tinkoffApiAdapter.updateInstrumentInfo(currency));
    }

    @Test
    void updateInstrumentInfo_Etf() {
        String ticker = "TICKER";
        Etf etf = new Etf();
        etf.setTicker(ticker);
        when(investApi.getInstrumentsService()).thenReturn(instrumentsService);
        when(instrumentsService.getAllEtfsSync()).thenReturn(List.of(ru.tinkoff.piapi.contract.v1.Etf.newBuilder().setTicker(ticker).build()));

        tinkoffApiAdapter.updateInstrumentInfo(etf);

        verify(instrumentsService).getAllEtfsSync();
    }

    @Test
    void updateInstrumentInfo_EtfThrows() {
        Etf etf = new Etf();
        when(investApi.getInstrumentsService()).thenReturn(instrumentsService);
        when(instrumentsService.getAllEtfsSync()).thenReturn(List.of(ru.tinkoff.piapi.contract.v1.Etf.newBuilder().build()));

        assertThrows(SimpleTradingRobotRuntimeException.class, () -> tinkoffApiAdapter.updateInstrumentInfo(etf));
    }

    @Test
    void updateInstrumentInfo_Throws() {
        Instrument instrument = new Instrument();

        assertThrows(SimpleTradingRobotRuntimeException.class, () -> tinkoffApiAdapter.updateInstrumentInfo(instrument));
    }
}
