package com.simple.trading.robot.executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MarketListenerExecutorTest {

    @InjectMocks
    private MarketListenerExecutor executor;
    @Mock
    private ThreadPoolExecutor threadPoolExecutor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(executor, "corePoolSize", 1);
        ReflectionTestUtils.setField(executor, "maxPoolSize", 1);
        ReflectionTestUtils.setField(executor, "secondsToLive", 1);
        ReflectionTestUtils.setField(executor, "executor", threadPoolExecutor);
    }

    @Test
    void init() {
        executor.init();

        Object object = ReflectionTestUtils.getField(executor, "executor");

        assertNotNull(object);
        assertEquals(ThreadPoolExecutor.class, object.getClass());
    }

    @Test
    void execute() {
        Runnable runnable = () -> System.out.println("test runnable!");

        executor.execute(runnable);

        verify(threadPoolExecutor).execute(runnable);
    }
}
