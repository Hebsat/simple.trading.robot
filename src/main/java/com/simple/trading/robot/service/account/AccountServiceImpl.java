package com.simple.trading.robot.service.account;

import com.simple.trading.robot.dto.api.AccountInfo;
import com.simple.trading.robot.dto.properties.StrategyTemplate;
import com.simple.trading.robot.exception.SimpleTradingRobotPropertiesException;
import com.simple.trading.robot.service.api.ApiSelector;
import com.simple.trading.robot.service.api.ApiType;
import com.simple.trading.robot.strategy.StrategyPrepareService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final ApiSelector apiSelector;
    private final StrategyPrepareService strategyPrepareService;

    @Override
    public AccountInfo getAccountInfo(ApiType api, boolean isSandbox, String accountId) {
        return apiSelector.getApiByType(api).getAccountInfo(accountId, isSandbox);
    }

    @PostConstruct
    public void init() {
        checkAccountsAvailability(strategyPrepareService.getStrategiesByApiByAccounts());
        Map<String, List<StrategyTemplate>> strategiesByAccounts = strategyPrepareService.getStrategiesByApiByAccounts().values().stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        strategiesByAccounts.forEach(this::checkPercentagesInOneAccount);
        strategiesByAccounts.forEach(this::checkAccountAmounts);
    }

    private void checkAccountsAvailability(Map<ApiType, Map<String, List<StrategyTemplate>>> accounts) {
        accounts.forEach((apiType, strategiesByAccount) -> {
            Map<Boolean, List<String>> accountsBySandboxMode = strategiesByAccount.entrySet().stream()
                    .collect(Collectors.groupingBy(e -> e.getValue().getFirst().getAccount().isSandbox(), Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

            if (accountsBySandboxMode.containsKey(Boolean.TRUE)) {
                if (accountsBySandboxMode.get(Boolean.TRUE).contains("open")) {
                    String newAccountId = apiSelector.getApiByType(apiType).openSandboxAccount();
                    log.warn("Открыт новый счет песочницы {}", newAccountId);
                    throw new SimpleTradingRobotPropertiesException(String.format("Открыт новый счет песочницы %s", newAccountId));
                }

                Set<String> accountsIds = apiSelector.getApiByType(apiType).getAccountIds(true);
                accountsBySandboxMode.get(Boolean.TRUE).forEach(name -> {
                    if (!accountsIds.contains(name)) {
                        log.error("Счета {} в песочнице не существует. Чтобы создать новый счет ввести \"open\" в поле account. Найдены следующие счета: {}", name, accountsIds);
                        throw new SimpleTradingRobotPropertiesException(String.format("Счет %s не найден",name));
                    }
                });
            }

            if (accountsBySandboxMode.containsKey(Boolean.FALSE)) {
                Set<String> accountsIds = apiSelector.getApiByType(apiType).getAccountIds(false);
                accountsBySandboxMode.get(Boolean.FALSE).forEach(name -> {
                    if (!accountsIds.contains(name)) {
                        log.trace("Счета {} не существует. Найдены следующие счета: {}", name, accountsIds);
                        throw new SimpleTradingRobotPropertiesException(String.format("Счет %s не найден",name));
                    }
                });
            }
        });
    }

    private void checkPercentagesInOneAccount(String accountId, List<StrategyTemplate> strategies) {
        BigDecimal sum = strategies.stream()
                .map(s -> s.getAccount().getMaxPercent())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.debug("На счете {} торговля {} процентов от общей суммы", accountId, sum);
        if (sum.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new SimpleTradingRobotPropertiesException(String.format("Сумма maxPercent на счете %s превышает 100%%", accountId));
        }
    }

    private void checkAccountAmounts(String accountId, List<StrategyTemplate> strategies) {
        AccountInfo accountInfo = getAccountInfo(strategies.getFirst().getAccount().getApi(), strategies.getFirst().getAccount().isSandbox(), accountId);
        BigDecimal totalAmount = accountInfo.getAmount();
        log.trace("На счете {} доступно для торгровли {} {}", accountId, totalAmount, accountInfo.getCurrency());
        strategies.forEach(s -> correctTradingLineAmount(s, totalAmount));
    }

    private void correctTradingLineAmount(StrategyTemplate strategyTemplate, BigDecimal accountAmount) {
        BigDecimal currentLimit = accountAmount.multiply(strategyTemplate.getAccount().getMaxPercent()).divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
        if (strategyTemplate.getAccount().getMaxSum().compareTo(BigDecimal.ZERO) == 0 || strategyTemplate.getAccount().getMaxSum().compareTo(currentLimit) > 0) {
            strategyTemplate.getAccount().setMaxSum(currentLimit);
        }
        log.info("Для стратегии {} максимальная сумма для тогрговли {}", strategyTemplate.getName(), currentLimit);
    }
}
