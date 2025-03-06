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
@Table(name = "currencies")
public class Currency extends Instrument {

    public Currency() {
        setType(InstrumentType.CURRENCY);
    }
}
