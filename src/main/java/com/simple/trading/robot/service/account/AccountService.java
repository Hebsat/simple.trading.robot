package com.simple.trading.robot.service.account;

import com.simple.trading.robot.dto.api.AccountInfo;

public interface AccountService {

    AccountInfo getAccountInfo(boolean isSandbox, String accountId);
}
