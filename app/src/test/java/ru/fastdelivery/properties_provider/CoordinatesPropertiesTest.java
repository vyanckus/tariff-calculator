package ru.fastdelivery.properties_provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.properties.provider.CoordinatesProperties;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinatesPropertiesTest {

    CoordinatesProperties properties;

    @BeforeEach
    void setUp() {
        properties = new CoordinatesProperties();

        CoordinatesProperties.Range latRange = new CoordinatesProperties.Range();
        latRange.setMin(BigDecimal.valueOf(45));
        latRange.setMax(BigDecimal.valueOf(65));

        CoordinatesProperties.Range lonRange = new CoordinatesProperties.Range();
        lonRange.setMin(BigDecimal.valueOf(30));
        lonRange.setMax(BigDecimal.valueOf(96));

        properties.setLatitude(latRange);
        properties.setLongitude(lonRange);
    }

    @Test
    void whenGetMinLatitude_thenReturnFromConfig() {
        assertThat(properties.getMinLatitude()).isEqualByComparingTo("45");
    }

    @Test
    void whenGetMaxLatitude_thenReturnFromConfig() {
        assertThat(properties.getMaxLatitude()).isEqualByComparingTo("65");
    }

    @Test
    void whenGetMinLongitude_thenReturnFromConfig() {
        assertThat(properties.getMinLongitude()).isEqualByComparingTo("30");
    }

    @Test
    void whenGetMaxLongitude_thenReturnFromConfig() {
        assertThat(properties.getMaxLongitude()).isEqualByComparingTo("96");
    }
}
