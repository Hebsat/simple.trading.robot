package com.simple.trading.robot.service.api.tinkoff;

import com.google.protobuf.Timestamp;
import com.simple.trading.robot.dto.CandleInterval;
import com.simple.trading.robot.entity.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tinkoff.piapi.contract.v1.Bond;
import ru.tinkoff.piapi.contract.v1.Etf;
import ru.tinkoff.piapi.contract.v1.Future;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static com.simple.trading.robot.dto.CandleInterval.FIVE_MINUTES;
import static com.simple.trading.robot.dto.CandleInterval.ONE_DAY;
import static com.simple.trading.robot.dto.CandleInterval.ONE_HOUR;
import static com.simple.trading.robot.dto.CandleInterval.ONE_MINUTE;
import static com.simple.trading.robot.dto.CandleInterval.ONE_WEEK;
import static com.simple.trading.robot.dto.CandleInterval.TEN_MINUTES;
import static com.simple.trading.robot.dto.CandleInterval.THIRTY_MINUTES;
import static com.simple.trading.robot.entity.Currency.RUB;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

@ExtendWith(MockitoExtension.class)
class TinkoffConverterUtilTest {

    private final BigDecimal bigDecimal = BigDecimal.valueOf(12.34);
    private final String inputCurrency = "rub";
    private final Currency currency = RUB;

    @Test
    void quotationToBigDecimal() {
        Quotation quotation = Quotation.newBuilder()
                .setUnits(bigDecimal.longValue())
                .setNano(bigDecimal.remainder(BigDecimal.ONE).movePointRight(9).abs().intValue())
                .build();

        BigDecimal result = TinkoffConverterUtil.toBigDecimal(quotation).stripTrailingZeros();

        assertEquals(bigDecimal, result);
    }

    @Test
    void quotationToBigDecimal_zeroValue() {
        Quotation quotation = Quotation.newBuilder().setUnits(0).setNano(0).build();

        BigDecimal result = TinkoffConverterUtil.toBigDecimal(quotation).stripTrailingZeros();

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void quotationToBigDecimal_nullValue() {
        BigDecimal result = TinkoffConverterUtil.toBigDecimal((Quotation) (null));

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testToBigDecimal() {
        MoneyValue moneyValue = toMoneyValue(bigDecimal);

        BigDecimal result = TinkoffConverterUtil.toBigDecimal(moneyValue).stripTrailingZeros();

        assertEquals(bigDecimal, result);
    }

    @Test
    void testToBigDecimal_zeroValue() {
        MoneyValue moneyValue = MoneyValue.newBuilder().setUnits(0).setNano(0).build();

        BigDecimal result = TinkoffConverterUtil.toBigDecimal(moneyValue).stripTrailingZeros();

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testToBigDecimal_nullValue() {
        BigDecimal result = TinkoffConverterUtil.toBigDecimal((MoneyValue) null).stripTrailingZeros();

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void toMoneyValue() {
        MoneyValue moneyValue = TinkoffConverterUtil.toMoneyValue(BigDecimal.valueOf(12.34), RUB);

        assertEquals(12, moneyValue.getUnits());
        assertEquals(340_000_000, moneyValue.getNano());
        assertEquals("rub", moneyValue.getCurrency());
    }

    @Test
    void timestampToTime() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(offsetDateTime.toEpochSecond()).setNanos(offsetDateTime.getNano()).build();

        OffsetDateTime result = TinkoffConverterUtil.timestampToTime(timestamp);

        assertEquals(offsetDateTime, result);
    }

    @ParameterizedTest
    @MethodSource("getLocalToTinkoffIntervals")
    void convertToTinkoffCandleInterval(CandleInterval from, ru.tinkoff.piapi.contract.v1.CandleInterval to) {
        ru.tinkoff.piapi.contract.v1.CandleInterval result = TinkoffConverterUtil.convertToTinkoffCandleInterval(from);

        assertEquals(to, result);
    }

    private static Stream<Arguments> getLocalToTinkoffIntervals() {
        return Stream.of(
                Arguments.of(ONE_MINUTE, CANDLE_INTERVAL_1_MIN),
                Arguments.of(FIVE_MINUTES, CANDLE_INTERVAL_5_MIN),
                Arguments.of(TEN_MINUTES, CANDLE_INTERVAL_10_MIN),
                Arguments.of(THIRTY_MINUTES, CANDLE_INTERVAL_30_MIN),
                Arguments.of(ONE_HOUR, CANDLE_INTERVAL_HOUR),
                Arguments.of(ONE_DAY, CANDLE_INTERVAL_DAY),
                Arguments.of(ONE_WEEK, CANDLE_INTERVAL_WEEK));
    }

    @ParameterizedTest
    @MethodSource("getSubscriptionToLocalIntervals")
    void convertToLocalCandleInterval(SubscriptionInterval from, CandleInterval to) {
        CandleInterval result = TinkoffConverterUtil.convertToLocalCandleInterval(from);

        assertEquals(to, result);
    }

    private static Stream<Arguments> getSubscriptionToLocalIntervals() {
        return Stream.of(
                Arguments.of(SUBSCRIPTION_INTERVAL_ONE_MINUTE, ONE_MINUTE),
                Arguments.of(SUBSCRIPTION_INTERVAL_FIVE_MINUTES, FIVE_MINUTES),
                Arguments.of(SUBSCRIPTION_INTERVAL_10_MIN, TEN_MINUTES),
                Arguments.of(SUBSCRIPTION_INTERVAL_30_MIN, THIRTY_MINUTES),
                Arguments.of(SUBSCRIPTION_INTERVAL_ONE_HOUR, ONE_HOUR),
                Arguments.of(SUBSCRIPTION_INTERVAL_ONE_DAY, ONE_DAY),
                Arguments.of(SUBSCRIPTION_INTERVAL_WEEK, ONE_WEEK));
    }

    @ParameterizedTest
    @MethodSource("getCurrencies")
    void toCurrency(String from, Currency to) {
        Currency result = TinkoffConverterUtil.toCurrency(from);

        assertEquals(to, result);
    }

    private static Stream<Arguments> getCurrencies() {
        return Stream.of(
                Arguments.of("rub", RUB),
                Arguments.of("invalid currency", null)
        );
    }

    @Test
    void updateBond() {
        Bond tBond = Bond.newBuilder().setUid(UUID.randomUUID().toString()).setCurrency(inputCurrency).build();
        com.simple.trading.robot.entity.instrument.Bond bond = new com.simple.trading.robot.entity.instrument.Bond();

        TinkoffConverterUtil.updateBond(tBond, bond);

        assertEquals(tBond.getUid(), bond.getTinkoffId());
        assertEquals(currency, bond.getCurrency());
    }

    @Test
    void updateShare() {
        Share tShare = Share.newBuilder().setUid(UUID.randomUUID().toString()).setCurrency(inputCurrency).build();
        com.simple.trading.robot.entity.instrument.Share share = new com.simple.trading.robot.entity.instrument.Share();

        TinkoffConverterUtil.updateShare(tShare, share);

        assertEquals(tShare.getUid(), share.getTinkoffId());
        assertEquals(currency, share.getCurrency());
    }

    @Test
    void updateFuture() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        BigDecimal buyingMargin = BigDecimal.valueOf(new Random().nextInt());
        BigDecimal sellingMargin = BigDecimal.valueOf(new Random().nextInt());
        Future tFuture = Future.newBuilder()
                .setUid(UUID.randomUUID().toString())
                .setCurrency(inputCurrency)
                .setExpirationDate(toTimestamp(offsetDateTime))
                .setInitialMarginOnBuy(toMoneyValue(buyingMargin))
                .setInitialMarginOnSell(toMoneyValue(sellingMargin))
                .build();
        com.simple.trading.robot.entity.instrument.Future future = new com.simple.trading.robot.entity.instrument.Future();

        TinkoffConverterUtil.updateFuture(tFuture, future);

        assertEquals(tFuture.getUid(), future.getTinkoffId());
        assertEquals(currency, future.getCurrency());
        assertEquals(tFuture.getExpirationDate(), toTimestamp(future.getExpirationDate()));
        assertEquals(tFuture.getInitialMarginOnBuy(), toMoneyValue(future.getBuyingMargin()));
        assertEquals(tFuture.getInitialMarginOnSell(), toMoneyValue(future.getSellingMargin()));
    }

    @Test
    void updateCurrency() {

        ru.tinkoff.piapi.contract.v1.Currency tCurrency = ru.tinkoff.piapi.contract.v1.Currency.newBuilder().setUid(UUID.randomUUID().toString()).setCurrency(inputCurrency).build();
        com.simple.trading.robot.entity.instrument.Currency localCurreny = new com.simple.trading.robot.entity.instrument.Currency();

        TinkoffConverterUtil.updateCurrency(tCurrency, localCurreny);

        assertEquals(tCurrency.getUid(), localCurreny.getTinkoffId());
        assertEquals(currency, localCurreny.getCurrency());
    }

    @Test
    void updateEtf() {
        Etf tEtf = Etf.newBuilder().setUid(UUID.randomUUID().toString()).setCurrency(inputCurrency).build();
        com.simple.trading.robot.entity.instrument.Etf etf = new com.simple.trading.robot.entity.instrument.Etf();

        TinkoffConverterUtil.updateEtf(tEtf, etf);

        assertEquals(tEtf.getUid(), etf.getTinkoffId());
        assertEquals(currency, etf.getCurrency());
    }

    private MoneyValue toMoneyValue(BigDecimal value) {
        return MoneyValue.newBuilder()
                .setUnits(value.longValue())
                .setNano(value.remainder(BigDecimal.ONE).movePointRight(9).abs().intValue())
                .build();
    }

    private Timestamp toTimestamp(OffsetDateTime time) {
        return Timestamp.newBuilder().setSeconds(time.toEpochSecond()).setNanos(time.getNano()).build();
    }
}
