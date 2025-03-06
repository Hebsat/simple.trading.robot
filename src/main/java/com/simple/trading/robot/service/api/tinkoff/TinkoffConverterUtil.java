package com.simple.trading.robot.service.api.tinkoff;

import com.google.protobuf.Timestamp;
import com.simple.trading.robot.entity.Currency;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.mapstruct.Named;
import ru.tinkoff.piapi.contract.v1.Bond;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.Etf;
import ru.tinkoff.piapi.contract.v1.Future;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;

import static com.simple.trading.robot.dto.CandleInterval.FIVE_MINUTES;
import static com.simple.trading.robot.dto.CandleInterval.ONE_DAY;
import static com.simple.trading.robot.dto.CandleInterval.ONE_HOUR;
import static com.simple.trading.robot.dto.CandleInterval.ONE_MINUTE;
import static com.simple.trading.robot.dto.CandleInterval.ONE_WEEK;
import static com.simple.trading.robot.dto.CandleInterval.TEN_MINUTES;
import static com.simple.trading.robot.dto.CandleInterval.THIRTY_MINUTES;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_10_MIN;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_1_MIN;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_30_MIN;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_5_MIN;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_DAY;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_HOUR;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_WEEK;
import static ru.tinkoff.piapi.contract.v1.SubscriptionInterval.SUBSCRIPTION_INTERVAL_10_MIN;
import static ru.tinkoff.piapi.contract.v1.SubscriptionInterval.SUBSCRIPTION_INTERVAL_30_MIN;
import static ru.tinkoff.piapi.contract.v1.SubscriptionInterval.SUBSCRIPTION_INTERVAL_FIVE_MINUTES;
import static ru.tinkoff.piapi.contract.v1.SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_DAY;
import static ru.tinkoff.piapi.contract.v1.SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_HOUR;
import static ru.tinkoff.piapi.contract.v1.SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE;
import static ru.tinkoff.piapi.contract.v1.SubscriptionInterval.SUBSCRIPTION_INTERVAL_WEEK;

@UtilityClass
public class TinkoffConverterUtil {

    private static final Map<com.simple.trading.robot.dto.CandleInterval, CandleInterval> TO_TINKOFF_INTERVAL = Map.of(
            ONE_MINUTE, CANDLE_INTERVAL_1_MIN,
            FIVE_MINUTES, CANDLE_INTERVAL_5_MIN,
            TEN_MINUTES, CANDLE_INTERVAL_10_MIN,
            THIRTY_MINUTES, CANDLE_INTERVAL_30_MIN,
            ONE_HOUR, CANDLE_INTERVAL_HOUR,
            ONE_DAY, CANDLE_INTERVAL_DAY,
            ONE_WEEK, CANDLE_INTERVAL_WEEK
    );

    private static final Map<SubscriptionInterval, com.simple.trading.robot.dto.CandleInterval> TO_LOCAL_INTERVAL = Map.of(
            SUBSCRIPTION_INTERVAL_ONE_MINUTE, ONE_MINUTE,
            SUBSCRIPTION_INTERVAL_FIVE_MINUTES, FIVE_MINUTES,
            SUBSCRIPTION_INTERVAL_10_MIN, TEN_MINUTES,
            SUBSCRIPTION_INTERVAL_30_MIN, THIRTY_MINUTES,
            SUBSCRIPTION_INTERVAL_ONE_HOUR, ONE_HOUR,
            SUBSCRIPTION_INTERVAL_ONE_DAY, ONE_DAY,
            SUBSCRIPTION_INTERVAL_WEEK, ONE_WEEK
    );

    private static final Map<String, Currency> TO_CURRENCY = Map.of(
            "rub", Currency.RUB
    );

    @Named("toBigDecimal")
    public static BigDecimal toBigDecimal(Quotation value) {
        return Objects.isNull(value) ?
                BigDecimal.ZERO :
                BigDecimal.valueOf(value.getUnits()).add(BigDecimal.valueOf(value.getNano(), 9));
    }

    public static BigDecimal toBigDecimal(MoneyValue moneyValue) {
        return Objects.isNull(moneyValue) ?
                BigDecimal.ZERO :
                BigDecimal.valueOf(moneyValue.getUnits()).add(BigDecimal.valueOf(moneyValue.getNano(), 9));
    }

    @Named("timestampToTime")
    public static OffsetDateTime timestampToTime(@NonNull Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return OffsetDateTime.ofInstant(instant, ZoneId.of("Europe/Moscow"));
    }

    public static CandleInterval convertToTinkoffCandleInterval(@NonNull com.simple.trading.robot.dto.CandleInterval candleInterval) {
        return TO_TINKOFF_INTERVAL.get(candleInterval);
    }

    public static com.simple.trading.robot.dto.CandleInterval convertToLocalCandleInterval(@NonNull SubscriptionInterval subscriptionInterval) {
        return TO_LOCAL_INTERVAL.get(subscriptionInterval);
    }

    @Named("toCurrency")
    public static Currency toCurrency(@NonNull String currency) {
        return TO_CURRENCY.get(currency);
    }

    public static void updateBond(@NonNull Bond tBond, @NonNull com.simple.trading.robot.entity.instrument.Bond bond) {
        bond.setTinkoffId(tBond.getUid());
        bond.setCurrency(toCurrency(tBond.getCurrency()));
    }

    public static void updateShare(@NonNull Share tShare, @NonNull com.simple.trading.robot.entity.instrument.Share share) {
        share.setTinkoffId(tShare.getUid());
        share.setCurrency(toCurrency(tShare.getCurrency()));
    }

    public static void updateFuture(@NonNull Future tFuture, @NonNull com.simple.trading.robot.entity.instrument.Future future) {
        future.setTinkoffId(tFuture.getUid());
        future.setCurrency(toCurrency(tFuture.getCurrency()));
        future.setExpirationDate(timestampToTime(tFuture.getExpirationDate()));
        future.setBuyingMargin(toBigDecimal(tFuture.getInitialMarginOnBuy()));
        future.setSellingMargin(toBigDecimal(tFuture.getInitialMarginOnSell()));
    }

    public static void updateCurrency(@NonNull ru.tinkoff.piapi.contract.v1.Currency tCurrency, @NonNull com.simple.trading.robot.entity.instrument.Currency currency) {
        currency.setTinkoffId(tCurrency.getUid());
        currency.setCurrency(toCurrency(tCurrency.getCurrency()));
    }

    public static void updateEtf(@NonNull Etf tEtf, @NonNull com.simple.trading.robot.entity.instrument.Etf etf) {
        etf.setTinkoffId(tEtf.getUid());
        etf.setCurrency(toCurrency(tEtf.getCurrency()));
    }
}
