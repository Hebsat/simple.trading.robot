package com.simple.trading.robot.configuraton;

import com.simple.trading.robot.dto.properties.StrategyTemplate;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "simple-trading-robot")
public class SimpleTradingRobotProperties {

    private List<StrategyTemplate> strategies;
}
