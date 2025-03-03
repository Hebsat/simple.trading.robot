package com.simple.trading.robot.service.api;

public interface ApiSelector {

    MarketApi getApiByType(ApiType apiType);
}
