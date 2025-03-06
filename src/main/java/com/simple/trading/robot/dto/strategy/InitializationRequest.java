package com.simple.trading.robot.dto.strategy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InitializationRequest {

    private String accountId;
    private String strategyName;
    private InstrumentInfoRequest instrumentInfo;
}
