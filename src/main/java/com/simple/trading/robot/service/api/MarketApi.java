package com.simple.trading.robot.service.api;

import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.dto.api.OrderCommand;
import com.simple.trading.robot.dto.api.OrderExecuteResponse;
import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.entity.Currency;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.exception.SimpleTradingRobotRuntimeException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface MarketApi {

    ApiType getApiType();

    AccountInfo getAccountInfo(String accountId, boolean isSandbox);

    Set<String> getAccountIds(boolean isSandbox);

    String openSandboxAccount();

    boolean fillUpSandboxAccount(String accountId, BigDecimal amount, Currency currency);

    List<Candle> getInstrumentHistory(InstrumentInfoRequest instrumentInfoRequest);

    void listenInstruments(Collection<Instrument> instruments, Consumer<Candle> consumer) throws SimpleTradingRobotRuntimeException;

    <T extends Instrument> void updateInstrumentInfo(T instrument);

    OrderExecuteResponse executeOrder(OrderCommand orderCommand);

    OrderExecuteResponse executeStopLoss(OrderCommand orderCommand);

    OrderExecuteResponse moveStopLoss(OrderCommand orderCommand);
}
