package ru.fastdelivery.domain.delivery.route;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.coordinates.Point;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RouteTest {

    @Test
    @DisplayName("Создание маршрута с валидными точками")
    void whenValidPoints_thenObjectCreated() {
        Point departure = new Point(
                BigDecimal.valueOf(55.446008),
                BigDecimal.valueOf(65.339151)
        );
        Point destination = new Point(
                BigDecimal.valueOf(73.398660),
                BigDecimal.valueOf(55.027532)
        );

        Route route = new Route(departure, destination);

        assertThat(route.departure()).isEqualTo(departure);
        assertThat(route.destination()).isEqualTo(destination);
    }

    @Test
    @DisplayName("Создание маршрута с null departure -> исключение")
    void whenDepartureNull_thenException() {
        Point destination = new Point(
                BigDecimal.valueOf(73.398660),
                BigDecimal.valueOf(55.027532)
        );

        assertThatThrownBy(() -> new Route(null, destination))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Создание маршрута с null destination -> исключение")
    void whenDestinationNull_thenException() {
        Point departure = new Point(
                BigDecimal.valueOf(55.446008),
                BigDecimal.valueOf(65.339151)
        );

        assertThatThrownBy(() -> new Route(departure, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Расчет расстояния между точками")
    void testDistanceCalculation() {
        Point departure = new Point(
                BigDecimal.valueOf(55.446008),
                BigDecimal.valueOf(65.339151)
        );
        Point destination = new Point(
                BigDecimal.valueOf(73.398660),
                BigDecimal.valueOf(55.027532)
        );

        Route route = new Route(departure, destination);
        BigDecimal distance = route.distanceInKilometers();

        assertThat(distance).isBetween(BigDecimal.valueOf(2000), BigDecimal.valueOf(2150));
    }

    @Test
    @DisplayName("Расчет коэффициента расстояния для расстояния меньше 450 км")
    void testDistanceFactorWhenLessThanMin() {
        Point departure = new Point(
                BigDecimal.valueOf(55.446008),
                BigDecimal.valueOf(65.339151)
        );
        Point destination = new Point(
                BigDecimal.valueOf(55.5),
                BigDecimal.valueOf(65.5)
        );

        Route route = new Route(departure, destination);
        BigDecimal factor = route.distanceFactor();

        assertThat(factor).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Расчет коэффициента расстояния для расстояния больше 450 км")
    void testDistanceFactorWhenMoreThanMin() {
        Point departure = new Point(
                BigDecimal.valueOf(55.446008),
                BigDecimal.valueOf(65.339151)
        );
        Point destination = new Point(
                BigDecimal.valueOf(73.398660),
                BigDecimal.valueOf(55.027532)
        );

        Route route = new Route(departure, destination);
        BigDecimal distance = route.distanceInKilometers();
        BigDecimal factor = route.distanceFactor();

        BigDecimal expectedFactor = distance.divide(BigDecimal.valueOf(450), 2, BigDecimal.ROUND_HALF_UP);

        assertThat(factor).isEqualByComparingTo(expectedFactor);
    }
}
