package com.simple.trading.robot.dto.properties;

import com.simple.trading.robot.dto.strategy.StrategyType;
import lombok.Data;

import java.util.Map;

@Data
public class StrategyTemplate {

    private boolean enabled;
    private String name;
    private StrategyType strategyType;
    private String instrument;
    private AccountDto account;
    private Map<String, Object> details;

    @Override
    public String toString() {
        return "\nStrategyTemplate{" +
                "\n    enabled=" + enabled +
                ",\n    name='" + name + '\'' +
                ",\n    strategyType=" + strategyType +
                ",\n    instrument='" + instrument + '\'' +
                ",\n    account=" + account +
                ",\n    details=" + details +
                '}';
    }
}
