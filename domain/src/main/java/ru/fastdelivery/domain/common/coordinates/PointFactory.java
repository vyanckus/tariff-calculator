package ru.fastdelivery.domain.common.coordinates;

import java.math.BigDecimal;

public class PointFactory {

    private final CoordinatesPropertiesProvider properties;

    public PointFactory(CoordinatesPropertiesProvider properties) {
        this.properties = properties;
    }

    public Point create(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude must not be null");
        }
        if (latitude.compareTo(properties.getMinLatitude()) < 0 ||
                latitude.compareTo(properties.getMaxLatitude()) > 0) {
            throw new IllegalArgumentException("Latitude must be between " +
                    properties.getMinLatitude() + " and " + properties.getMaxLatitude());
        }
        if (longitude.compareTo(properties.getMinLongitude()) < 0 ||
                longitude.compareTo(properties.getMaxLongitude()) > 0) {
            throw new IllegalArgumentException("Longitude must be between " +
                    properties.getMinLongitude() + " and " + properties.getMaxLongitude());
        }
        return new Point(latitude, longitude);
    }
}
