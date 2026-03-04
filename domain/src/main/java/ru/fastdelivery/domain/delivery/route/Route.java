package ru.fastdelivery.domain.delivery.route;

import ru.fastdelivery.domain.common.coordinates.Point;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Маршрут доставки от пункта отправления до пункта назначения
 *
 * @param departure   пункт отправления
 * @param destination пункт назначения
 */
public record Route(Point departure, Point destination) {

    private static final BigDecimal EARTH_RADIUS = BigDecimal.valueOf(6372795);
    private static final int DISTANCE_SCALE = 0;
    private static final BigDecimal MIN_DISTANCE = BigDecimal.valueOf(450);

    public Route {
        if (departure == null || destination == null) {
            throw new IllegalArgumentException("Departure and destination must not be null");
        }
    }

    /**
     * Расчет расстояния между точками по формуле гаверсинусов
     * @return расстояние в километрах
     */
    public BigDecimal distanceInKilometers() {
        double lat1 = Math.toRadians(departure.latitude().doubleValue());
        double lat2 = Math.toRadians(destination.latitude().doubleValue());
        double lon1 = Math.toRadians(departure.longitude().doubleValue());
        double lon2 = Math.toRadians(destination.longitude().doubleValue());

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = Math.pow(Math.sin(deltaLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(deltaLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceMeters = EARTH_RADIUS.doubleValue() * c;
        BigDecimal distanceKm = BigDecimal.valueOf(distanceMeters / 1000)
                .setScale(DISTANCE_SCALE, RoundingMode.HALF_UP);

        return distanceKm;
    }

    /**
     * Расчет коэффициента расстояния (Расстояние / 450, но не менее 1)
     * @return коэффициент для применения к базовой стоимости
     */
    public BigDecimal distanceFactor() {
        BigDecimal distance = distanceInKilometers();
        if (distance.compareTo(MIN_DISTANCE) < 0) {
            return BigDecimal.ONE;
        }
        return distance.divide(MIN_DISTANCE, 2, RoundingMode.HALF_UP);
    }
}
