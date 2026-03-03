package ru.fastdelivery.domain.common.length;

import java.math.BigInteger;

/**
 * Длина в миллиметрах.
 *
 * @param lengthMillimeters длина в миллиметрах.
 */
public record Length(BigInteger lengthMillimeters) implements Comparable<Length> {

    private static final Length ZERO = new Length(BigInteger.ZERO);
    private static final BigInteger FIFTY_MM = BigInteger.valueOf(50);

    public Length {
        if (isLessThanZero(lengthMillimeters)) {
            throw new IllegalArgumentException("Length cannot be below Zero!");
        }
    }

    private static boolean isLessThanZero(BigInteger value) {
        return BigInteger.ZERO.compareTo(value) > 0;
    }

    public static Length zero() {
        return ZERO;
    }

    @Override
    public int compareTo(Length o) {
        return this.lengthMillimeters.compareTo(o.lengthMillimeters);
    }

    /**
     * Проверяет, превышает ли текущая длина заданную максимальную.
     *
     * @param maxLength максимально допустимая длина
     * @return true, если текущая длина больше максимальной
     */
    public boolean longerThan(Length maxLength) {
        return this.lengthMillimeters.compareTo(maxLength.lengthMillimeters) > 0;
    }

    /**
     * Округляет длину вверх до ближайшего значения, кратного 50 мм.
     *
     * @return новая длина, округленная вверх кратно 50
     */
    public Length roundUpToFifty() {
        BigInteger remainder = lengthMillimeters.remainder(FIFTY_MM);

        if (remainder.equals(BigInteger.ZERO)) {
            return this;
        }

        BigInteger roundedUp = lengthMillimeters.add(FIFTY_MM.subtract(remainder));
        return new Length(roundedUp);
    }
}
