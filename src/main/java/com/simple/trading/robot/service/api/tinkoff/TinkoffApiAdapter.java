package com.simple.trading.robot.service.api.tinkoff;

import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.dto.api.OrderCommand;
import com.simple.trading.robot.dto.api.OrderExecuteResponse;
import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.entity.Currency;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.exception.SimpleTradingRobotRuntimeException;
import com.simple.trading.robot.service.api.ApiType;
import com.simple.trading.robot.service.api.MarketApi;
import com.simple.trading.robot.service.api.tinkoff.mapper.AccountInfoMapper;
import com.simple.trading.robot.service.api.tinkoff.mapper.CandleMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Portfolio;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil.convertToLocalCandleInterval;
import static com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil.timestampToTime;
import static com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil.toBigDecimal;
import static com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil.updateBond;
import static com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil.updateCurrency;
import static com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil.updateEtf;
import static com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil.updateFuture;
import static com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil.updateShare;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "simple-trading-robot.tinkoff-api.enabled", havingValue = "true", matchIfMissing = true)
public class TinkoffApiAdapter implements MarketApi {

    private final AccountInfoMapper accountInfoMapper;
    private final CandleMapper candleMapper;

    @Value("${simple-trading-robot.tinkoff-api.token}")
    private String token;
    @Value("${simple-trading-robot.tinkoff-api.sandbox-token}")
    private String sandboxToken;

    private InvestApi tinkoffApi;
    private InvestApi sandboxApi;

    private final Map<String, Instrument> instrumentCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        tinkoffApi = InvestApi.create(token, "simple-trading-robot");
        sandboxApi = InvestApi.createSandbox(sandboxToken, "simple-trading-robot");
    }

    private <T> T invokeApi(Function<InvestApi, T> function, boolean isSandbox) {
        return function.apply(isSandbox ? sandboxApi : tinkoffApi);
    }

    @Override
    public ApiType getApiType() {
        return ApiType.TINKOFF_API;
    }

    @Override
    public AccountInfo getAccountInfo(String accountId, boolean isSandbox) {
        Portfolio portfolio = invokeApi(i -> i.getOperationsService().getPortfolioSync(accountId), isSandbox);

        return accountInfoMapper.mapToAccountInfo(accountId, portfolio, isSandbox, ApiType.TINKOFF_API);
    }

    @Override
    public Set<String> getAccountIds(boolean isSandbox) {
        return isSandbox ?
                invokeApi(i -> i.getSandboxService().getAccountsSync(), true).stream().map(Account::getId).collect(Collectors.toSet()) :
                invokeApi(i -> i.getUserService().getAccountsSync(), false).stream().map(Account::getId).collect(Collectors.toSet());
    }

    @Override
    public String openSandboxAccount() {
        return invokeApi(i -> i.getSandboxService().openAccountSync(), true);
    }

    @Override
    public boolean fillUpSandboxAccount(String accountId, BigDecimal amount, Currency currency) {
        MoneyValue accepted = sandboxApi.getSandboxService().payInSync(accountId, TinkoffConverterUtil.toMoneyValue(amount, currency));
        return TinkoffConverterUtil.toBigDecimal(accepted).compareTo(amount) == 0;
    }

    @Override
    public List<Candle> getInstrumentHistory(InstrumentInfoRequest instrumentInfoRequest) {
        Instant now = Instant.now();
        CandleInterval interval = TinkoffConverterUtil.convertToTinkoffCandleInterval(instrumentInfoRequest.getInterval());
        List<HistoricCandle> tinkoffCandles = tinkoffApi.getMarketDataService()
                .getCandlesSync(instrumentInfoRequest.getInstrument().getTinkoffId(), now.minus(instrumentInfoRequest.getDuration()), now, interval);

        return tinkoffCandles.stream().map(c -> candleMapper.mapToCandle(instrumentInfoRequest, c)).toList();
    }

    @Override
    public void listenInstruments(Collection<Instrument> instruments, Consumer<Candle> consumer) throws SimpleTradingRobotRuntimeException {
        instruments.forEach(instrument -> instrumentCache.put(instrument.getTinkoffId(), instrument));
        String streamId = UUID.randomUUID().toString();
        log.info("Запуск стрима {} для инструментов: {}", streamId, instruments);
        tinkoffApi.getMarketDataStreamService()
                .newStream(streamId, response -> {
                    if (response.hasCandle()) {
                        Candle candle = Candle.builder()
                                .instrument(instrumentCache.get(response.getCandle().getInstrumentUid()))
                                .interval(convertToLocalCandleInterval(response.getCandle().getInterval()))
                                .openingPrice(toBigDecimal(response.getCandle().getOpen()))
                                .closingPrice(toBigDecimal(response.getCandle().getClose()))
                                .highestPrice(toBigDecimal(response.getCandle().getHigh()))
                                .lowestPrice(toBigDecimal(response.getCandle().getLow()))
                                .openingTime(timestampToTime(response.getCandle().getTime()))
                                .build();

                        consumer.accept(candle);
                    }
                }, e -> {
                    log.error("Стрим {} завершен с ошибкой {}", streamId, e.getClass().getName());
                    throw new SimpleTradingRobotRuntimeException(e);
                })
                .subscribeCandles(instruments.stream().map(Instrument::getTinkoffId).toList());
    }

    @Override
    public void updateInstrumentInfo(Instrument instrument) {
        switch (instrument) {
            case com.simple.trading.robot.entity.instrument.Bond bond -> updateBond(
                    tinkoffApi.getInstrumentsService().getAllBondsSync().stream().filter(i -> i.getTicker().equals(instrument.getTicker())).findFirst()
                            .orElseThrow(() -> new SimpleTradingRobotRuntimeException(String.format("Не найдена облигация с тикером %s", bond.getTicker()))), bond);
            case com.simple.trading.robot.entity.instrument.Share share -> updateShare(
                    tinkoffApi.getInstrumentsService().getAllSharesSync().stream().filter(i -> i.getTicker().equals(instrument.getTicker())).findFirst()
                            .orElseThrow(() -> new SimpleTradingRobotRuntimeException(String.format("Не найдена акция с тикером %s", share.getTicker()))), share);
            case com.simple.trading.robot.entity.instrument.Future future -> updateFuture(
                    tinkoffApi.getInstrumentsService().getAllFuturesSync().stream().filter(i -> i.getTicker().equals(instrument.getTicker())).findFirst()
                            .orElseThrow(() -> new SimpleTradingRobotRuntimeException(String.format("Не найден фьючерс с тикером %s", future.getTicker()))), future);
            case com.simple.trading.robot.entity.instrument.Currency currency -> updateCurrency(
                    tinkoffApi.getInstrumentsService().getAllCurrenciesSync().stream().filter(i -> i.getTicker().equals(instrument.getTicker())).findFirst()
                            .orElseThrow(() -> new SimpleTradingRobotRuntimeException(String.format("Не найдена валюта с тикером %s", currency.getTicker()))), currency);
            case com.simple.trading.robot.entity.instrument.Etf etf -> updateEtf(
                    tinkoffApi.getInstrumentsService().getAllEtfsSync().stream().filter(i -> i.getTicker().equals(instrument.getTicker())).findFirst()
                            .orElseThrow(() -> new SimpleTradingRobotRuntimeException(String.format("Не найден ETF с тикером %s", etf.getTicker()))), etf);
            default ->
                    throw new SimpleTradingRobotRuntimeException(String.format("Некорректный тип инструмента %s", instrument.getClass().getName()));
        }
    }

    @Override
    public OrderExecuteResponse executeOrder(OrderCommand orderCommand) {
        return null;
    }

    @Override
    public OrderExecuteResponse executeStopLoss(OrderCommand orderCommand) {
        return null;
    }

    @Override
    public OrderExecuteResponse moveStopLoss(OrderCommand orderCommand) {
        return null;
    }
}
