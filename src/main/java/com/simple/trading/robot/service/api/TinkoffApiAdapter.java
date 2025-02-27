package com.simple.trading.robot.service.api;

import com.simple.trading.robot.AccountInfoMapper;
import com.simple.trading.robot.dto.api.AccountInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Portfolio;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TinkoffApiAdapter implements MarketApi {

    private final AccountInfoMapper accountInfoMapper;

    @Value("${simple-trading-robot.token}")
    private String token;
    @Value("${simple-trading-robot.sandbox-token}")
    private String sandboxToken;

    private InvestApi tinkoffApi;
    private InvestApi sandboxApi;

    @PostConstruct
    public void init() {
        tinkoffApi = InvestApi.create(token, "simple-trading-robot");
        sandboxApi = InvestApi.createSandbox(sandboxToken, "simple-trading-robot");
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
}
