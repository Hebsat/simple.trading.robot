package com.simple.trading.robot.executor;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class MarketListenerExecutor implements Executor {

    @Value("${simple-trading-robot.threads.always-active}")
    private int corePoolSize;
    @Value("${simple-trading-robot.threads.max-size}")
    private int maxPoolSize;
    @Value("${simple-trading-robot.threads.seconds-to-live}")
    private int secondsToLive;

    private ThreadPoolExecutor executor;

    @PostConstruct
    public void init() {
        executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, secondsToLive, TimeUnit.SECONDS, new LinkedBlockingDeque<>(maxPoolSize));
    }

    @Override
    public void execute(@NonNull Runnable command) {
        executor.execute(command);
    }
}
