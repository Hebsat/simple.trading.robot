package com.simple.trading.robot.strategy;

import com.simple.trading.robot.configuraton.SimpleTradingRobotProperties;
import com.simple.trading.robot.dto.properties.AccountTemplate;
import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.exception.SimpleTradingRobotPropertiesException;
import com.simple.trading.robot.service.api.ApiType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StrategyPrepareServiceImplTest {

    @InjectMocks
    private StrategyPrepareServiceImpl strategyPrepareService;

    @Mock
    private SimpleTradingRobotProperties properties;

    private final AccountTemplate accountTemplate = AccountTemplate.builder()
            .api(ApiType.TINKOFF_API)
            .id("123")
            .build();
    private final StrategyTemplate templateDisabled = StrategyTemplate.builder().build();
    private final StrategyTemplate template1 = StrategyTemplate.builder()
            .enabled(true)
            .name("First")
            .account(accountTemplate)
            .build();
    private final StrategyTemplate template2 = StrategyTemplate.builder()
            .enabled(true)
            .name("Second")
            .account(accountTemplate)
            .build();
    private final StrategyTemplate templateWithDuplicateName = StrategyTemplate.builder()
            .enabled(true)
            .name("First")
            .account(accountTemplate)
            .build();

    @Test
    void init() {
        when(properties.getStrategies()).thenReturn(List.of(templateDisabled, template1, template2));

        strategyPrepareService.init();

        Map<String, StrategyTemplate> strategiesByNames = (Map<String, StrategyTemplate>) ReflectionTestUtils.getField(strategyPrepareService, "strategiesByNames");
        Map<ApiType, Map<String, List<StrategyTemplate>>> strategiesByApiByAccounts = strategyPrepareService.getStrategiesByApiByAccounts();

        assertEquals(2, strategiesByNames.size());
        assertTrue(strategiesByNames.containsKey(template1.getName()));
        assertTrue(strategiesByNames.containsKey(template2.getName()));
        assertEquals(template1, strategiesByNames.get(template1.getName()));
        assertEquals(template2, strategiesByNames.get(template2.getName()));

        assertEquals(1, strategiesByApiByAccounts.size());
        assertTrue(strategiesByApiByAccounts.containsKey(ApiType.TINKOFF_API));
        assertEquals(1, strategiesByApiByAccounts.get(ApiType.TINKOFF_API).size());
        assertTrue(strategiesByApiByAccounts.get(ApiType.TINKOFF_API).containsKey(accountTemplate.getId()));
        assertEquals(2, strategiesByApiByAccounts.get(ApiType.TINKOFF_API).get(accountTemplate.getId()).size());
        assertTrue(strategiesByApiByAccounts.get(ApiType.TINKOFF_API).get(accountTemplate.getId()).contains(template1));
        assertTrue(strategiesByApiByAccounts.get(ApiType.TINKOFF_API).get(accountTemplate.getId()).contains(template2));
    }

    @Test
    void init_throwsException() {
        when(properties.getStrategies()).thenReturn(List.of(templateDisabled, template1, templateWithDuplicateName));

        assertThrows(SimpleTradingRobotPropertiesException.class, () -> strategyPrepareService.init());
    }

    @Test
    void getStrategies() {
        ReflectionTestUtils.setField(strategyPrepareService, "strategiesByNames", Map.of(template1.getName(), template1, template2.getName(), template2));

        List<StrategyTemplate> result = strategyPrepareService.getStrategies();

        assertEquals(2, result.size());
        assertTrue(result.contains(template1));
        assertTrue(result.contains(template2));
    }

    @Test
    void getStrategiesByApiByAccounts() {
        ReflectionTestUtils.setField(strategyPrepareService, "strategiesByApiByAccounts",
                Map.of(ApiType.TINKOFF_API, Map.of(accountTemplate.getId(), List.of(template1, template2))));

        Map<ApiType, Map<String, List<StrategyTemplate>>> result = strategyPrepareService.getStrategiesByApiByAccounts();

        assertEquals(1, result.size());
        assertTrue(result.containsKey(ApiType.TINKOFF_API));
        assertEquals(1, result.get(ApiType.TINKOFF_API).size());
        assertTrue(result.get(ApiType.TINKOFF_API).containsKey(accountTemplate.getId()));
        assertEquals(2, result.get(ApiType.TINKOFF_API).get(accountTemplate.getId()).size());
        assertTrue(result.get(ApiType.TINKOFF_API).get(accountTemplate.getId()).contains(template1));
        assertTrue(result.get(ApiType.TINKOFF_API).get(accountTemplate.getId()).contains(template2));
    }
}
