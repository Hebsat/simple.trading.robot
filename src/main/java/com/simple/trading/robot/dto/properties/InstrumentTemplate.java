package com.simple.trading.robot.dto.properties;

import com.simple.trading.robot.entity.InstrumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentTemplate {

    private String ticker;
    private InstrumentType type;
}
