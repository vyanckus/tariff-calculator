package ru.fastdelivery.domain.common.length;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LengthTest {

    @Test
    @DisplayName("Попытка создать отрицательную длину -> исключение")
    void whenMillimetersBelowZero_thenException() {
        var millimeters = new BigInteger("-1");
        assertThatThrownBy(() -> new Length(millimeters))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalsTypeWidth_same() {
        var length = new Length(new BigInteger("1000"));
        var lengthSame = new Length(new BigInteger("1000"));

        assertThat(length)
                .isEqualTo(lengthSame)
                .hasSameHashCodeAs(lengthSame);
    }

    @Test
    void equalsNull_false() {
        var length = new Length(new BigInteger("4"));

        assertThat(length).isNotEqualTo(null);
    }

    @ParameterizedTest
    @CsvSource({ "1, 1000, -1",
            "199, 199, 0",
            "999, 50, 1" })
    void compareToTest(BigInteger low, BigInteger high, int expected) {
        var lengthLow = new Length(low);
        var lengthHigh = new Length(high);

        assertThat(lengthLow.compareTo(lengthHigh))
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Округление вверх до 50 мм")
    void roundUpToFiftyTest() {
        var length = new Length(new BigInteger("345"));
        var expected = new Length(new BigInteger("350"));

        assertThat(length.roundUpToFifty()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Первая длина больше второй -> true")
    void whenFirstLengthGreaterThanSecond_thenTrue() {
        var lengthBig = new Length(new BigInteger("1001"));
        var lengthSmall = new Length(new BigInteger("1000"));

        assertThat(lengthBig.longerThan(lengthSmall)).isTrue();
    }

    @Test
    @DisplayName("Первая длина меньше второй -> false")
    void whenFirstLengthLessThanSecond_thenFalse() {
        var lengthSmall = new Length(new BigInteger("1000"));
        var lengthBig = new Length(new BigInteger("1001"));

        assertThat(lengthSmall.longerThan(lengthBig)).isFalse();
    }

    @Test
    @DisplayName("zero() возвращает длину 0")
    void zeroTest() {
        assertThat(Length.zero()).isEqualTo(new Length(BigInteger.ZERO));
    }
}
