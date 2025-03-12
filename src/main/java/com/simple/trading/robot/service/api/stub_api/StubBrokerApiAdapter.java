package com.simple.trading.robot.service.api.stub_api;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Service
@ConditionalOnProperty(name = "simple-trading-robot.stub-api.enabled", havingValue = "true")
public class StubBrokerApiAdapter implements MarketApi {

    @Override
    public ApiType getApiType() {
        return ApiType.STUB_API;
    }

    @Override
    public AccountInfo getAccountInfo(String accountId, boolean isSandbox) {
        return AccountInfo.builder().accountId("123").amount(BigDecimal.TEN).currency(Currency.RUB).build();
    }

    @Override
    public Set<String> getAccountIds(boolean isSandbox) {
        return Set.of("123");
    }

    @Override
    public String openSandboxAccount() {
        return "";
    }

    @Override
    public boolean fillUpSandboxAccount(String accountId, BigDecimal amount, Currency currency) {
        return false;
    }

    @Override
    public List<Candle> getInstrumentHistory(InstrumentInfoRequest instrumentInfoRequest) {
        return List.of();
    }

    @Override
    public void listenInstruments(Collection<Instrument> instruments, Consumer<Candle> consumer) throws SimpleTradingRobotRuntimeException {
        //some code
    }

    @Override
    public <T extends Instrument> void updateInstrumentInfo(T instrument) {
        instrument.setCurrency(Currency.RUB);
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
