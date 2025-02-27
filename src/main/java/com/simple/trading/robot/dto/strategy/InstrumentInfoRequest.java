package com.simple.trading.robot.dto.strategy;

import com.simple.trading.robot.dto.CandleInterval;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
@Builder
public class InstrumentInfoRequest {

    private String name;
    private CandleInterval interval;
    private Duration duration;
}
