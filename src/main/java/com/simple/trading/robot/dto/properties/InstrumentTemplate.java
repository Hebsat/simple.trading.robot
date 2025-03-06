package com.simple.trading.robot.dto.properties;

import com.simple.trading.robot.entity.InstrumentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstrumentTemplate {

    private String ticker;
    private InstrumentType type;
}
