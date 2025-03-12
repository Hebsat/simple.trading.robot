package com.simple.trading.robot.service.market;

import com.simple.trading.robot.dto.api.OrderCommand;
import com.simple.trading.robot.dto.api.OrderExecuteResponse;

public interface OrderExecutorService {

    OrderExecuteResponse executeOrderCommand(OrderCommand orderCommand);
}
