package ru.fastdelivery.properties_provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.properties.provider.VolumePriceProperties;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VolumePricePropertiesTest {

    public static final BigDecimal PER_CUBIC_METER = BigDecimal.valueOf(5000);
    public static final String RUB = "RUB";

    final CurrencyFactory currencyFactory = mock(CurrencyFactory.class);
    VolumePriceProperties properties;

    @BeforeEach
    void init() {
        properties = new VolumePriceProperties();
        properties.setCurrencyFactory(currencyFactory);
        properties.setPerCubicMeter(PER_CUBIC_METER);

        var currency = mock(Currency.class);
        when(currency.getCode()).thenReturn(RUB);
        when(currencyFactory.create(RUB)).thenReturn(currency);
    }

    @Test
    void whenCallCostPerCubicMeter_thenRequestFromConfig() {
        var actual = properties.costPerCubicMeter();

        verify(currencyFactory).create("RUB");
        assertThat(actual.amount()).isEqualByComparingTo(PER_CUBIC_METER);
        assertThat(actual.currency().getCode()).isEqualTo("RUB");
    }
}
