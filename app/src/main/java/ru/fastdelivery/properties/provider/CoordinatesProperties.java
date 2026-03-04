package ru.fastdelivery.properties.provider;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.fastdelivery.domain.common.coordinates.CoordinatesPropertiesProvider;

import java.math.BigDecimal;

@ConfigurationProperties("coordinates")
@Getter
@Setter
public class CoordinatesProperties implements CoordinatesPropertiesProvider {

    private Range latitude;
    private Range longitude;

    @Override
    public BigDecimal getMinLatitude() {
        return latitude.getMin();
    }

    @Override
    public BigDecimal getMaxLatitude() {
        return latitude.getMax();
    }

    @Override
    public BigDecimal getMinLongitude() {
        return longitude.getMin();
    }

    @Override
    public BigDecimal getMaxLongitude() {
        return longitude.getMax();
    }

    @Getter
    @Setter
    public static class Range {
        private BigDecimal min;
        private BigDecimal max;
    }
}
