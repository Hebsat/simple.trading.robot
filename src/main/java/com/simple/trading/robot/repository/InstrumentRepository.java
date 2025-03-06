package com.simple.trading.robot.repository;

import com.simple.trading.robot.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

    Optional<Instrument> getInstrumentByTicker(String ticker);
}
