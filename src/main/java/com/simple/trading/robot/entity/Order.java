package com.simple.trading.robot.entity;

import lombok.Getter;

@Getter
public class Order {

    private long id;
    private String version;
    private String accountId;
    private String instrument;

}
