package com.simple.trading.robot.service.api.tinkoff;

import com.google.protobuf.Timestamp;
import lombok.experimental.UtilityClass;
import org.mapstruct.Named;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.Map;

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

    private static final Map<com.simple.trading.robot.dto.CandleInterval, CandleInterval> TO_TINKOFF_INTERVAL = new EnumMap<>(Map.of(
            ONE_MINUTE, CANDLE_INTERVAL_1_MIN,
            FIVE_MINUTES, CANDLE_INTERVAL_5_MIN,
            TEN_MINUTES, CANDLE_INTERVAL_10_MIN,
            THIRTY_MINUTES, CANDLE_INTERVAL_30_MIN,
            ONE_HOUR, CANDLE_INTERVAL_HOUR,
            ONE_DAY, CANDLE_INTERVAL_DAY,
            ONE_WEEK, CANDLE_INTERVAL_WEEK
    ));

    private static final Map<SubscriptionInterval, com.simple.trading.robot.dto.CandleInterval> TO_LOCAL_INTERVAL = new EnumMap<>(Map.of(
            SUBSCRIPTION_INTERVAL_ONE_MINUTE, ONE_MINUTE,
            SUBSCRIPTION_INTERVAL_FIVE_MINUTES, FIVE_MINUTES,
            SUBSCRIPTION_INTERVAL_10_MIN, TEN_MINUTES,
            SUBSCRIPTION_INTERVAL_30_MIN, THIRTY_MINUTES,
            SUBSCRIPTION_INTERVAL_ONE_HOUR, ONE_HOUR,
            SUBSCRIPTION_INTERVAL_ONE_DAY, ONE_DAY,
            SUBSCRIPTION_INTERVAL_WEEK, ONE_WEEK
    ));

    @Named("toBigDecimal")
    public static BigDecimal toBigDecimal(Quotation value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value.getUnits()).add(BigDecimal.valueOf(value.getNano(), 9));
    }

    @Named("timestampToTime")
    public static OffsetDateTime timestampToTime(Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return OffsetDateTime.ofInstant(instant, ZoneId.of("Europe/Moscow"));
    }

    public static CandleInterval convertToTinkoffCandleInterval(com.simple.trading.robot.dto.CandleInterval candleInterval) {
        return TO_TINKOFF_INTERVAL.get(candleInterval);
    }

    public static com.simple.trading.robot.dto.CandleInterval convertToLocalCandleInterval(SubscriptionInterval subscriptionInterval) {
        return TO_LOCAL_INTERVAL.get(subscriptionInterval);
    }
}
