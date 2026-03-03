package ru.fastdelivery.domain.delivery.pack;

import ru.fastdelivery.domain.common.length.Length;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Внешние габариты упаковки
 *
 * @param length длина
 * @param width  ширина
 * @param height высота
 */
public record OuterDimensions(Length length, Length width, Length height) {

    private static final Length MAX_DIMENSION = new Length(BigInteger.valueOf(1500));
    private static final BigInteger MM_IN_M3 = BigInteger.valueOf(1_000_000_000L);
    private static final int VOLUME_SCALE = 4;

    public OuterDimensions {
        if (isDimensionTooLong(length) || isDimensionTooLong(width) || isDimensionTooLong(height)) {
            throw new IllegalArgumentException("Dimension cannot be more than 1500 mm");
        }
    }

    private static boolean isDimensionTooLong(Length dimension) {
        return dimension.longerThan(MAX_DIMENSION);
    }

    /**
     * Вычисляет объем в кубических метрах
     */
    public BigDecimal volumeCubicMeters() {
        Length roundedLength = length.roundUpToFifty();
        Length roundedWidth = width.roundUpToFifty();
        Length roundedHeight = height.roundUpToFifty();

        BigInteger volumeMM = roundedLength.lengthMillimeters()
                .multiply(roundedWidth.lengthMillimeters())
                .multiply(roundedHeight.lengthMillimeters());

        return new BigDecimal(volumeMM)
                .divide(new BigDecimal(MM_IN_M3), VOLUME_SCALE, RoundingMode.HALF_UP);
    }
}
