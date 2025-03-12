package com.simple.trading.robot.service.impl;

import com.simple.trading.robot.dto.api.OrderCommand;
import com.simple.trading.robot.dto.api.Candle;
import com.simple.trading.robot.dto.api.OrderExecuteResponse;
import com.simple.trading.robot.entity.Order;
import com.simple.trading.robot.service.TradingService;
import com.simple.trading.robot.service.market.OrderExecutorService;
import com.simple.trading.robot.strategy.StrategyService;
import com.simple.trading.robot.strategy.strategies.Strategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingServiceImpl implements TradingService {

    private final StrategyService strategyService;
    private final OrderExecutorService orderExecutorService;

    @Override
    public void handleCurrentPrice(Candle candle) {
        log.warn("I've got a candle for instrument {}. Current price is {}", candle.getInstrument().getTicker(), candle.getClosingPrice());

        List<Strategy> strategiesNeededThisCandle = strategyService.getStrategiesByInstrument(candle.getInstrument());
        strategiesNeededThisCandle.forEach(strategy -> sendCandleToStrategy(strategy, candle));

    }

    private void sendCandleToStrategy(Strategy strategy, Candle candle) {
        OrderCommand orderCommand = strategy.handleInstantInfo(candle);
        if (OrderCommand.CommandType.WAIT.equals(orderCommand.getCommandType())) {
            return;
        }
        log.info("For strategy {} - {}", strategy.getName(), orderCommand.getCommandType().toString());
        OrderExecuteResponse response = orderExecutorService.executeOrderCommand(orderCommand);
        log.info(response.toString());
    }
}
