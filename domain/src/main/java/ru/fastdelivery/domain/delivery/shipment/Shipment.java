package ru.fastdelivery.domain.delivery.shipment;

import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.pack.OuterDimensions;
import ru.fastdelivery.domain.delivery.route.Route;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @param packages упаковки в грузе
 * @param currency валюта объявленная для груза
 * @param route    маршрут доставки
 */
public record Shipment(
        List<Pack> packages,
        Currency currency,
        Route route
) {
    public Shipment {
        if (packages == null || packages.isEmpty()) {
            throw new IllegalArgumentException("Packages must not be null or empty");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }
    }

    public Shipment(List<Pack> packages, Currency currency) {
        this(packages, currency, null);
    }

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
