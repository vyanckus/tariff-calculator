package ru.fastdelivery.domain.delivery.pack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.length.Length;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OuterDimensionsTest {

    @Test
    @DisplayName("Создание с допустимыми размерами -> успешно")
    void whenDimensionsValid_thenObjectCreated() {
        Length length = new Length(BigInteger.valueOf(345));
        Length width = new Length(BigInteger.valueOf(589));
        Length height = new Length(BigInteger.valueOf(234));

        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        assertThat(dimensions.length()).isEqualTo(length);
        assertThat(dimensions.width()).isEqualTo(width);
        assertThat(dimensions.height()).isEqualTo(height);
    }

    @Test
    @DisplayName("Размер больше 1500 мм -> исключение")
    void whenDimensionTooLong_thenException() {
        Length length = new Length(BigInteger.valueOf(1501));
        Length width = new Length(BigInteger.valueOf(500));
        Length height = new Length(BigInteger.valueOf(500));

        assertThatThrownBy(() -> new OuterDimensions(length, width, height))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dimension cannot be more than 1500 mm");
    }

    @Test
    @DisplayName("Расчет объема в кубических метрах")
    void testVolumeCubicMeters() {
        Length length = new Length(BigInteger.valueOf(345));
        Length width = new Length(BigInteger.valueOf(589));
        Length height = new Length(BigInteger.valueOf(234));

        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        // 350 * 600 * 250 / 1_000_000_000 = 0.0525
        assertThat(dimensions.volumeCubicMeters())
                .isEqualByComparingTo("0.0525");
    }

    @Test
    @DisplayName("Расчет объема с размерами кратными 50")
    void testVolumeWithExactFifty() {
        Length length = new Length(BigInteger.valueOf(350));
        Length width = new Length(BigInteger.valueOf(600));
        Length height = new Length(BigInteger.valueOf(250));

        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        // 350 * 600 * 250 / 1_000_000_000 = 0.0525
        assertThat(dimensions.volumeCubicMeters())
                .isEqualByComparingTo("0.0525");
    }
}
