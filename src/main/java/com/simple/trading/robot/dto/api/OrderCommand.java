package com.simple.trading.robot.dto.api;

import com.simple.trading.robot.entity.Instrument;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderCommand {

    private CommandType commandType;
    private Instrument instrument;
    private int lots;

    public enum CommandType {
        BUY,
        SELL,
        WAIT
    }
}
