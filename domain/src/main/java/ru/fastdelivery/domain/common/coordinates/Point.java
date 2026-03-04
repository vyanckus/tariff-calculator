package ru.fastdelivery.domain.common.coordinates;

import java.math.BigDecimal;

/**
 * Географическая точка с широтой и долготой
 *
 * @param latitude  широта
 * @param longitude долгота
 */
public record Point(BigDecimal latitude, BigDecimal longitude) {

    public Point {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude must not be null");
        }
    }
}
