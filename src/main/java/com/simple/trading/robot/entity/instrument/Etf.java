package com.simple.trading.robot.entity.instrument;

import com.simple.trading.robot.entity.Instrument;
import com.simple.trading.robot.entity.InstrumentType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "etfs")
public class Etf extends Instrument {

    public Etf() {
        setType(InstrumentType.ETF);
    }
}
