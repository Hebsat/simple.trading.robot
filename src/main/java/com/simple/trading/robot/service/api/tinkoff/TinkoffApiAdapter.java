package com.simple.trading.robot.service.api.tinkoff;

import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.exception.SimpleTradingRobotRuntimeException;
import com.simple.trading.robot.service.api.ApiType;
import com.simple.trading.robot.service.api.MarketApi;
import com.simple.trading.robot.service.api.tinkoff.mapper.AccountInfoMapper;
import com.simple.trading.robot.service.api.tinkoff.mapper.CandleMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Portfolio;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil.convertToLocalCandleInterval;
import static com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil.timestampToTime;
import static com.simple.trading.robot.service.api.tinkoff.TinkoffConverterUtil.toBigDecimal;

@Service
@RequiredArgsConstructor
public class TinkoffApiAdapter implements MarketApi {

    private final AccountInfoMapper accountInfoMapper;
    private final CandleMapper candleMapper;

    @Value("${simple-trading-robot.tinkoff-api.token}")
    private String token;
    @Value("${simple-trading-robot.tinkoff-api.sandbox-token}")
    private String sandboxToken;

    private InvestApi tinkoffApi;
    private InvestApi sandboxApi;

    @PostConstruct
    public void init() {
        tinkoffApi = InvestApi.create(token, "simple-trading-robot");
        sandboxApi = InvestApi.createSandbox(sandboxToken, "simple-trading-robot");
    }

    @Override
    public ApiType getApiType() {
        return ApiType.TINKOFF_API;
    }

    @Override
    public AccountInfo getAccountInfo(String accountId, boolean isSandbox) {
        Portfolio portfolio = invokeApi(i -> i.getOperationsService().getPortfolioSync(accountId), isSandbox);

        return accountInfoMapper.mapToAccountInfo(portfolio);
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

    private <T> T invokeApi(Function<InvestApi, T> function, boolean isSandbox) {
        return function.apply(isSandbox ? sandboxApi : tinkoffApi);
    }

    @Override
    public List<Candle> getInstrumentHistory(InstrumentInfoRequest instrumentInfoRequest) {
        Instant now = Instant.now();
        CandleInterval interval = TinkoffConverterUtil.convertToTinkoffCandleInterval(instrumentInfoRequest.getInterval());
        List<HistoricCandle> tinkoffCandles = tinkoffApi.getMarketDataService()
                .getCandlesSync(instrumentInfoRequest.getName(), now.minus(instrumentInfoRequest.getDuration()), now, interval);

        return tinkoffCandles.stream().map(c -> candleMapper.mapToCandle(instrumentInfoRequest, c)).toList();
    }

    @Override
    public void listenInstruments(Set<String> instruments, Consumer<Candle> consumer) throws SimpleTradingRobotRuntimeException {
        tinkoffApi.getMarketDataStreamService()
                .newStream("candles_stream", response -> {
                    if (response.hasCandle()) {
                        Candle candle = Candle.builder()
                                .instrument(response.getCandle().getFigi())
                                .interval(convertToLocalCandleInterval(response.getCandle().getInterval()))
                                .openingPrice(toBigDecimal(response.getCandle().getOpen()))
                                .closingPrice(toBigDecimal(response.getCandle().getClose()))
                                .highestPrice(toBigDecimal(response.getCandle().getHigh()))
                                .lowestPrice(toBigDecimal(response.getCandle().getLow()))
                                .openingTime(timestampToTime(response.getCandle().getTime()))
                                .build();

                        consumer.accept(candle);
                    }
                }, e -> { e.printStackTrace();
                    throw new SimpleTradingRobotRuntimeException(e.getLocalizedMessage());
                })
                .subscribeCandles(new ArrayList<>(instruments));
    }
}
