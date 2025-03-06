package com.simple.trading.robot.entity;

import com.simple.trading.robot.dto.strategy.StrategyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    @Column(nullable = false, length = 36)
    private String accountId;

    @ManyToOne
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @Column(precision = 20, scale = 9)
    private BigDecimal buyingPrice;

    @Column(precision = 20, scale = 9)
    private BigDecimal buyingCommission;

    private OffsetDateTime buyingTime;

    @Column(precision = 20, scale = 9)
    private BigDecimal sellingPrice;

    @Column(precision = 20, scale = 9)
    private BigDecimal sellingComission;

    private OffsetDateTime sellingTime;

    private String strategyName;

    @Enumerated(EnumType.STRING)
    private StrategyType strategyType;

    @Column(precision = 20, scale = 9)
    private BigDecimal orderProfit;

    public boolean isClosed() {
        return Objects.nonNull(orderProfit);
    }

    public boolean isProfit() {
        return Objects.nonNull(orderProfit) && BigDecimal.ZERO.compareTo(orderProfit) <= 0;
    }
}
