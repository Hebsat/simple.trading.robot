package com.simple.trading.robot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "instruments")
@Inheritance(strategy = InheritanceType.JOINED)
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    @Column(unique = true, nullable = false, length = 10)
    private String ticker;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InstrumentType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(length = 36)
    private String tinkoffId;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime created;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updated;
}
