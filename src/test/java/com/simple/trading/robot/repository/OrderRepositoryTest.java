package com.simple.trading.robot.repository;

import com.simple.trading.robot.entity.Currency;
import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.entity.Order;
import com.simple.trading.robot.entity.instrument.Bond;
import com.simple.trading.robot.entity.instrument.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;

    @Test
    void findAllByInstrumentAndAccountIdAndStrategyNameAndOrderProfitIsNull() {
        Instrument instrument1 = new Future();
        instrument1.setTicker("QWERTY");
        instrument1.setCurrency(Currency.RUB);
        Instrument instrument2 = new Bond();
        instrument2.setTicker("ZXCVBN");
        instrument2.setCurrency(Currency.RUB);
        instrumentRepository.saveAll(List.of(instrument1, instrument2));

        String accountId = "123";
        String strategyName = "strategy_name";
        Order order1 = new Order();
        order1.setInstrument(instrument1);
        order1.setAccountId(accountId);
        order1.setStrategyName(strategyName);
        Order order2 = new Order();
        order2.setInstrument(instrument2);
        order2.setAccountId(accountId);
        order2.setStrategyName(strategyName);
        Order order3 = new Order();
        order3.setInstrument(instrument1);
        order3.setAccountId("321");
        order3.setStrategyName(strategyName);
        Order order4 = new Order();
        order4.setInstrument(instrument1);
        order4.setAccountId(accountId);
        order4.setStrategyName("another_strategy_name");
        Order order5 = new Order();
        order5.setInstrument(instrument1);
        order5.setAccountId(accountId);
        order5.setStrategyName(strategyName);
        order5.setOrderProfit(BigDecimal.ONE);
        orderRepository.saveAll(List.of(order1, order2, order3, order4, order5));

        List<Order> orders = orderRepository.findAllByInstrumentAndAccountIdAndStrategyNameAndOrderProfitIsNull(instrument1, accountId, strategyName);

        assertEquals(1, orders.size());
        assertEquals(orders.getFirst(), order1 );
    }
}
