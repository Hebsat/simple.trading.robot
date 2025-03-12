package com.simple.trading.robot.service.market;

import com.simple.trading.robot.dto.api.OrderCommand;
import com.simple.trading.robot.dto.api.OrderExecuteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderExecutorServiceImpl implements OrderExecutorService {

    @Override
    public OrderExecuteResponse executeOrderCommand(OrderCommand orderCommand) {
        return null;
    }
}
