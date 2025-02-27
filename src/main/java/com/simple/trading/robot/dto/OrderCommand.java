package com.simple.trading.robot.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderCommand {

    private CommandType commandType;
    private String instrument;
    private int lots;

    public enum CommandType {
        BUY,
        SELL,
        WAIT
    }
}
