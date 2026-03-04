package ru.fastdelivery.usecase;

import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.coordinates.Point;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.length.Length;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.OuterDimensions;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.route.Route;
import ru.fastdelivery.domain.delivery.shipment.Shipment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TariffCalculateUseCaseTest {

    final WeightPriceProvider weightPriceProvider = mock(WeightPriceProvider.class);
    final VolumePriceProvider volumePriceProvider = mock(VolumePriceProvider.class);
    final Currency currency = new CurrencyFactory(code -> true).create("RUB");

    final TariffCalculateUseCase tariffCalculateUseCase =
            new TariffCalculateUseCase(weightPriceProvider, volumePriceProvider);

    @Test
    @DisplayName("Расчет стоимости доставки по весу -> успешно")
    void whenCalculatePriceByWeight_thenSuccess() {
        var minimalPrice = new Price(BigDecimal.TEN, currency);
        var pricePerKg = new Price(BigDecimal.valueOf(100), currency);
        var pricePerCubicM = new Price(BigDecimal.valueOf(50), currency);

        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);
        when(weightPriceProvider.costPerKg()).thenReturn(pricePerKg);
        when(volumePriceProvider.costPerCubicMeter()).thenReturn(pricePerCubicM);

        var shipment = new Shipment(
                List.of(new Pack(new Weight(BigInteger.valueOf(1200)))),
                currency
        );

        var expectedPrice = new Price(BigDecimal.valueOf(120), currency);

        var actualPrice = tariffCalculateUseCase.calc(shipment);

        assertThat(actualPrice).usingRecursiveComparison()
                .withComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .isEqualTo(expectedPrice);
    }

    @Test
    @DisplayName("Расчет стоимости доставки по объему -> успешно")
    void whenCalculatePriceByVolume_thenSuccess() {
        var minimalPrice = new Price(BigDecimal.TEN, currency);
        var pricePerKg = new Price(BigDecimal.valueOf(100), currency);
        var pricePerCubicM = new Price(BigDecimal.valueOf(5000), currency);

        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);
        when(weightPriceProvider.costPerKg()).thenReturn(pricePerKg);
        when(volumePriceProvider.costPerCubicMeter()).thenReturn(pricePerCubicM);

        Length length = new Length(BigInteger.valueOf(345));
        Length width = new Length(BigInteger.valueOf(589));
        Length height = new Length(BigInteger.valueOf(234));
        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        var shipment = new Shipment(
                List.of(new Pack(new Weight(BigInteger.valueOf(1000)), dimensions)),
                currency
        );

        var expectedPrice = new Price(BigDecimal.valueOf(262.5), currency);

        var actualPrice = tariffCalculateUseCase.calc(shipment);

        assertThat(actualPrice).usingRecursiveComparison()
                .withComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .isEqualTo(expectedPrice);
    }

    @Test
    @DisplayName("Минимальная стоимость -> успешно")
    void whenMinimalPrice_thenSuccess() {
        BigDecimal minimalValue = BigDecimal.TEN;
        var minimalPrice = new Price(minimalValue, currency);
        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);

        var actual = tariffCalculateUseCase.minimalPrice();

        assertThat(actual).isEqualTo(minimalPrice);
    }

    @Test
    @DisplayName("Расчет стоимости с маршрутом (расстояние > 450 км)")
    void whenRouteWithLongDistance_thenPriceMultipliedByFactor() {
        Price minimalPrice = new Price(BigDecimal.TEN, currency);
        Price pricePerKg = new Price(BigDecimal.valueOf(100), currency);
        Price pricePerCubicM = new Price(BigDecimal.valueOf(50), currency);

        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);
        when(weightPriceProvider.costPerKg()).thenReturn(pricePerKg);
        when(volumePriceProvider.costPerCubicMeter()).thenReturn(pricePerCubicM);

        Point departure = new Point(
                BigDecimal.valueOf(55.446008),
                BigDecimal.valueOf(65.339151)
        );
        Point destination = new Point(
                BigDecimal.valueOf(73.398660),
                BigDecimal.valueOf(55.027532)
        );
        Route route = new Route(departure, destination);

        Pack pack = new Pack(new Weight(BigInteger.valueOf(1000))); // 1 кг
        Shipment shipment = new Shipment(
                List.of(pack),
                currency,
                route
        );

        BigDecimal expectedAmount = new BigDecimal("456.00");
        Price expectedPrice = new Price(expectedAmount, currency);

        Price actualPrice = tariffCalculateUseCase.calc(shipment);

        assertThat(actualPrice).usingRecursiveComparison()
                .withComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .isEqualTo(expectedPrice);
    }

    @Test
    @DisplayName("Расчет стоимости с маршрутом (расстояние < 450 км)")
    void whenRouteWithShortDistance_thenPriceNotMultiplied() {
        Price minimalPrice = new Price(BigDecimal.TEN, currency);
        Price pricePerKg = new Price(BigDecimal.valueOf(100), currency);
        Price pricePerCubicM = new Price(BigDecimal.valueOf(50), currency);

        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);
        when(weightPriceProvider.costPerKg()).thenReturn(pricePerKg);
        when(volumePriceProvider.costPerCubicMeter()).thenReturn(pricePerCubicM);

        Point departure = new Point(
                BigDecimal.valueOf(55.446008),
                BigDecimal.valueOf(65.339151)
        );
        Point destination = new Point(
                BigDecimal.valueOf(55.5),
                BigDecimal.valueOf(65.5)
        );
        Route route = new Route(departure, destination);

        Pack pack = new Pack(new Weight(BigInteger.valueOf(1000))); // 1 кг
        Shipment shipment = new Shipment(
                List.of(pack),
                currency,
                route
        );

        BigDecimal expectedAmount = new BigDecimal("100.00");
        Price expectedPrice = new Price(expectedAmount, currency);

        Price actualPrice = tariffCalculateUseCase.calc(shipment);

        assertThat(actualPrice).usingRecursiveComparison()
                .withComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .isEqualTo(expectedPrice);
    }

    @Test
    @DisplayName("Расчет стоимости с маршрутом, но минималка побеждает")
    void whenRouteButMinimalPriceWins() {
        Price minimalPrice = new Price(BigDecimal.valueOf(500), currency);
        Price pricePerKg = new Price(BigDecimal.valueOf(100), currency);
        Price pricePerCubicM = new Price(BigDecimal.valueOf(50), currency);

        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);
        when(weightPriceProvider.costPerKg()).thenReturn(pricePerKg);
        when(volumePriceProvider.costPerCubicMeter()).thenReturn(pricePerCubicM);

        Point departure = new Point(
                BigDecimal.valueOf(55.446008),
                BigDecimal.valueOf(65.339151)
        );
        Point destination = new Point(
                BigDecimal.valueOf(73.398660),
                BigDecimal.valueOf(55.027532)
        );
        Route route = new Route(departure, destination);

        Pack pack = new Pack(new Weight(BigInteger.valueOf(100))); // 0.1 кг
        Shipment shipment = new Shipment(
                List.of(pack),
                currency,
                route
        );

        BigDecimal expectedAmount = new BigDecimal("500.00");
        Price expectedPrice = new Price(expectedAmount, currency);

        Price actualPrice = tariffCalculateUseCase.calc(shipment);

        assertThat(actualPrice).usingRecursiveComparison()
                .withComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .isEqualTo(expectedPrice);
    }
}
