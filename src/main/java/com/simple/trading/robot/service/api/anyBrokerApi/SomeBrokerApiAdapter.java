package com.simple.trading.robot.service.api.anyBrokerApi;

import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.dto.strategy.InstrumentInfoRequest;
import com.simple.trading.robot.exception.SimpleTradingRobotRuntimeException;
import com.simple.trading.robot.service.api.ApiType;
import com.simple.trading.robot.service.api.MarketApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class SomeBrokerApiAdapter implements MarketApi {

    @Override
    public ApiType getApiType() {
        return ApiType.ANOTHER_API;
    }

    @Override
    public AccountInfo getAccountInfo(String accountId, boolean isSandbox) {
        return null;
    }

    @Override
    public Set<String> getAccountIds(boolean isSandbox) {
        return Set.of();
    }

    @Override
    public String openSandboxAccount() {
        return "";
    }

    @Override
    public List<Candle> getInstrumentHistory(InstrumentInfoRequest instrumentInfoRequest) {
        return List.of();
    }

    @Override
    public void listenInstruments(Set<String> instruments, Consumer<Candle> consumer) throws SimpleTradingRobotRuntimeException {
        //some code
    }
}
