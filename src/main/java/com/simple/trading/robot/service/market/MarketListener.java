package com.simple.trading.robot.service.market;

import com.simple.trading.robot.service.api.MarketApi;

import java.util.Set;

public interface MarketListener {

    void listenToMarket(MarketApi api, Set<String> instruments);
}
