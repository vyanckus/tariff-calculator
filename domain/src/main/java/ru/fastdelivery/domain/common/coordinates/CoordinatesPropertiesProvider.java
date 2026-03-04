package ru.fastdelivery.domain.common.coordinates;

import java.math.BigDecimal;

public interface CoordinatesPropertiesProvider {
    BigDecimal getMinLatitude();
    BigDecimal getMaxLatitude();
    BigDecimal getMinLongitude();
    BigDecimal getMaxLongitude();
}
