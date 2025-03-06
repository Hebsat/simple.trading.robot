package com.simple.trading.robot.service.impl;

import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.entity.Order;
import com.simple.trading.robot.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void getOpenedOrdersByInstrumentAndAccountAndStrategyName() {
        Order order = new Order();
        when(orderRepository.findAllByInstrumentAndAccountIdAndStrategyNameAndOrderProfitIsNull(any(Instrument.class), anyString(), anyString())).thenReturn(List.of(order));

        List<Order> result = orderService.getOpenedOrdersByInstrumentAndAccountAndStrategyName(new Instrument(), "accountId", "strategyName");

        assertEquals(1, result.size());
        assertTrue(result.contains(order));
    }
}
