package com.simple.trading.robot.repository;

import com.simple.trading.robot.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByInstrumentAndAccountIdAndStrategyNameAndOrderProfitIsNull(String instrument, String accountId, String strategyName);
}
