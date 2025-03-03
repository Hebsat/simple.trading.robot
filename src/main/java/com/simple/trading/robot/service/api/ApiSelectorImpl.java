package com.simple.trading.robot.service.api;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiSelectorImpl implements ApiSelector {

    private Map<ApiType, MarketApi> apiMap;
    private final List<MarketApi> apiList;

    @PostConstruct
    public void init() {
        apiMap = new EnumMap<>(ApiType.class);
        apiList.forEach(api -> apiMap.put(api.getApiType(), api));
    }

    @Override
    public MarketApi getApiByType(ApiType apiType) {
        return apiMap.get(apiType);
    }
}
