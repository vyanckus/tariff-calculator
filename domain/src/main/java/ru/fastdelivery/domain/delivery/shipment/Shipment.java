package ru.fastdelivery.domain.delivery.shipment;

import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.pack.OuterDimensions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @param packages упаковки в грузе
 * @param currency валюта объявленная для груза
 */
public record Shipment(
        List<Pack> packages,
        Currency currency
) {
    public Weight weightAllPackages() {
        return packages.stream()
                .map(Pack::weight)
                .reduce(Weight.zero(), Weight::add);
    }

    public BigDecimal volumeAllPackages() {
        return packages.stream()
                .map(Pack::dimensions)
                .filter(Objects::nonNull)
                .map(OuterDimensions::volumeCubicMeters)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
