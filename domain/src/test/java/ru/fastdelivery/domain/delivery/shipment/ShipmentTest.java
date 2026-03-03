package ru.fastdelivery.domain.delivery.shipment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.length.Length;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.OuterDimensions;
import ru.fastdelivery.domain.delivery.pack.Pack;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShipmentTest {

    private final CurrencyFactory currencyFactory = new CurrencyFactory(code -> true);

    @Test
    @DisplayName("Суммарный вес всех упаковок -> успешно")
    void whenSummarizingWeightOfAllPackages_thenReturnSum() {
        var weight1 = new Weight(BigInteger.TEN);
        var weight2 = new Weight(BigInteger.ONE);

        var packages = List.of(new Pack(weight1), new Pack(weight2));
        var shipment = new Shipment(packages, currencyFactory.create("RUB"));

        var massOfShipment = shipment.weightAllPackages();

        assertThat(massOfShipment.weightGrams()).isEqualByComparingTo(BigInteger.valueOf(11));
    }

    @Test
    @DisplayName("Упаковки без габаритов -> объем = 0")
    void whenPackagesWithoutDimensions_thenVolumeIsZero() {
        var weight = new Weight(BigInteger.TEN);
        var packages = List.of(new Pack(weight));

        var shipment = new Shipment(packages, currencyFactory.create("RUB"));

        assertThat(shipment.volumeAllPackages()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Упаковки с габаритами -> объем суммируется")
    void whenPackagesWithDimensions_thenVolumeIsCalculated() {
        Length length = new Length(BigInteger.valueOf(345));
        Length width = new Length(BigInteger.valueOf(589));
        Length height = new Length(BigInteger.valueOf(234));
        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        var weight = new Weight(BigInteger.TEN);
        var pack = new Pack(weight, dimensions);
        var packages = List.of(pack, pack); // две одинаковые упаковки

        var shipment = new Shipment(packages, currencyFactory.create("RUB"));

        // Объем одной упаковки: 0.0525 м³
        // Двух: 0.1050 м³
        assertThat(shipment.volumeAllPackages()).isEqualByComparingTo("0.1050");
    }

    @Test
    @DisplayName("Смешанные упаковки (с габаритами и без) -> учитываются только с габаритами")
    void whenMixedPackages_thenVolumeCalculatedOnlyForWithDimensions() {
        Length length = new Length(BigInteger.valueOf(345));
        Length width = new Length(BigInteger.valueOf(589));
        Length height = new Length(BigInteger.valueOf(234));
        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        var packWithDimensions = new Pack(new Weight(BigInteger.TEN), dimensions);
        var packWithoutDimensions = new Pack(new Weight(BigInteger.ONE));
        var packages = List.of(packWithDimensions, packWithoutDimensions);

        var shipment = new Shipment(packages, currencyFactory.create("RUB"));

        // Объем только одной упаковки с габаритами: 0.0525 м³
        assertThat(shipment.volumeAllPackages()).isEqualByComparingTo("0.0525");
    }
}
