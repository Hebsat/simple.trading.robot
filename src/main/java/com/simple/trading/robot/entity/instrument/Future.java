package com.simple.trading.robot.entity.instrument;

import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.entity.InstrumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "futures")
public class Future extends Instrument {

    public Future() {
        setType(InstrumentType.FUTURE);
    }

    private OffsetDateTime expirationDate;

    @Column(precision = 20, scale = 9)
    private BigDecimal buyingMargin;

    @Column(precision = 20, scale = 9)
    private BigDecimal sellingMargin;
}
