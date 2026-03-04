package ru.fastdelivery.domain.common.coordinates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PointFactoryTest {

    private PointFactory pointFactory;
    private CoordinatesPropertiesProvider properties;

    @BeforeEach
    void setUp() {
        properties = mock(CoordinatesPropertiesProvider.class);
        when(properties.getMinLatitude()).thenReturn(BigDecimal.valueOf(45));
        when(properties.getMaxLatitude()).thenReturn(BigDecimal.valueOf(65));
        when(properties.getMinLongitude()).thenReturn(BigDecimal.valueOf(30));
        when(properties.getMaxLongitude()).thenReturn(BigDecimal.valueOf(96));

        pointFactory = new PointFactory(properties);
    }

    @Test
    @DisplayName("Создание точки с координатами в допустимых пределах")
    void whenCoordinatesWithinBounds_thenObjectCreated() {
        BigDecimal latitude = BigDecimal.valueOf(55.446008);
        BigDecimal longitude = BigDecimal.valueOf(65.339151);

        Point point = pointFactory.create(latitude, longitude);

        assertThat(point.latitude()).isEqualTo(latitude);
        assertThat(point.longitude()).isEqualTo(longitude);
    }

    @Test
    @DisplayName("Создание точки с latitude меньше минимума -> исключение")
    void whenLatitudeLessThanMin_thenException() {
        BigDecimal latitude = BigDecimal.valueOf(44.999);
        BigDecimal longitude = BigDecimal.valueOf(65.339151);

        assertThatThrownBy(() -> pointFactory.create(latitude, longitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Latitude must be between");
    }

    @Test
    @DisplayName("Создание точки с latitude больше максимума -> исключение")
    void whenLatitudeMoreThanMax_thenException() {
        BigDecimal latitude = BigDecimal.valueOf(65.001);
        BigDecimal longitude = BigDecimal.valueOf(65.339151);

        assertThatThrownBy(() -> pointFactory.create(latitude, longitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Latitude must be between");
    }

    @Test
    @DisplayName("Создание точки с longitude меньше минимума -> исключение")
    void whenLongitudeLessThanMin_thenException() {
        BigDecimal latitude = BigDecimal.valueOf(55.446008);
        BigDecimal longitude = BigDecimal.valueOf(29.999);

        assertThatThrownBy(() -> pointFactory.create(latitude, longitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Longitude must be between");
    }

    @Test
    @DisplayName("Создание точки с longitude больше максимума -> исключение")
    void whenLongitudeMoreThanMax_thenException() {
        BigDecimal latitude = BigDecimal.valueOf(55.446008);
        BigDecimal longitude = BigDecimal.valueOf(96.001);

        assertThatThrownBy(() -> pointFactory.create(latitude, longitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Longitude must be between");
    }

    @Test
    @DisplayName("Создание точки с null latitude -> исключение")
    void whenLatitudeNull_thenException() {
        BigDecimal longitude = BigDecimal.valueOf(65.339151);

        assertThatThrownBy(() -> pointFactory.create(null, longitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Создание точки с null longitude -> исключение")
    void whenLongitudeNull_thenException() {
        BigDecimal latitude = BigDecimal.valueOf(55.446008);

        assertThatThrownBy(() -> pointFactory.create(latitude, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null");
    }
}
