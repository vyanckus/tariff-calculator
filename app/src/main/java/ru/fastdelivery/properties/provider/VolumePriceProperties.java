package ru.fastdelivery.properties.provider;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.usecase.VolumePriceProvider;

import java.math.BigDecimal;

@ConfigurationProperties("cost.rub.volume")
@Setter
public class VolumePriceProperties implements VolumePriceProvider {

    private BigDecimal perCubicMeter;

    @Autowired
    private CurrencyFactory currencyFactory;

    @Override
    public Price costPerCubicMeter() {
        return new Price(perCubicMeter, currencyFactory.create("RUB"));
    }
}
