package com.simple.trading.robot.service.api;

import com.simple.trading.robot.dto.api.AccountInfo;

import java.util.Set;

public interface MarketApi {

    AccountInfo getAccountInfo(String accountId, boolean isSandbox);

    Set<String> getAccountIds(boolean isSandbox);

    String openSandboxAccount();
}
