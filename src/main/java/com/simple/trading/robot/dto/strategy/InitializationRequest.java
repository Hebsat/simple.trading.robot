package com.simple.trading.robot.dto.strategy;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class InitializationRequest {

    private String accountId;
    private List<InstrumentInfoRequest> instrumentsInfo;
}
