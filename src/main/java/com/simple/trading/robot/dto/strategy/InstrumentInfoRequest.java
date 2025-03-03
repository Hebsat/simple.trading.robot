package com.simple.trading.robot.dto.strategy;

import com.simple.trading.robot.dto.CandleInterval;
import com.simple.trading.robot.service.api.ApiType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;

@Getter
@Builder
@ToString
public class InstrumentInfoRequest {

    private ApiType api;
    private String name;
    private CandleInterval interval;
    private Duration duration;
}
