package ru.fastdelivery.domain.common.coordinates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointTest {

    @Test
    @DisplayName("Создание точки с валидными координатами")
    void whenValidCoordinates_thenObjectCreated() {
        BigDecimal latitude = BigDecimal.valueOf(55.446008);
        BigDecimal longitude = BigDecimal.valueOf(65.339151);

        Point point = new Point(latitude, longitude);

        assertThat(point.latitude()).isEqualTo(latitude);
        assertThat(point.longitude()).isEqualTo(longitude);
    }

    @Test
    @DisplayName("Создание точки с null latitude -> исключение")
    void whenLatitudeNull_thenException() {
        BigDecimal longitude = BigDecimal.valueOf(65.339151);

        assertThatThrownBy(() -> new Point(null, longitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Создание точки с null longitude -> исключение")
    void whenLongitudeNull_thenException() {
        BigDecimal latitude = BigDecimal.valueOf(55.446008);

        assertThatThrownBy(() -> new Point(latitude, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null");
    }
}
