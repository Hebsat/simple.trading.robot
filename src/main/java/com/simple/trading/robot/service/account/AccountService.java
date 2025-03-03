package com.simple.trading.robot.service.account;

import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.service.api.ApiType;

public interface AccountService {

    AccountInfo getAccountInfo(ApiType api, boolean isSandbox, String accountId);
}
