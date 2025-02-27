package com.simple.trading.robot.service;

import com.simple.trading.robot.entity.Order;

import java.util.List;

public interface OrderService {

    List<Order> getOpenedOrdersByInstrumentAndAccount(String instrument, String accountId);
}
