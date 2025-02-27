package com.simple.trading.robot.service.impl;

import com.simple.trading.robot.entity.Order;
import com.simple.trading.robot.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Override
    public List<Order> getOpenedOrdersByInstrumentAndAccount(String instrument, String accountId) {
        return List.of();
    }
}
