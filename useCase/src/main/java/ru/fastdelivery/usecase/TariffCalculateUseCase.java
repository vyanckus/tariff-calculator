package ru.fastdelivery.usecase;

import lombok.RequiredArgsConstructor;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.delivery.shipment.Shipment;

import javax.inject.Named;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Named
@RequiredArgsConstructor
public class TariffCalculateUseCase {

    private final WeightPriceProvider weightPriceProvider;
    private final VolumePriceProvider volumePriceProvider;

    public Price calc(Shipment shipment) {
        BigDecimal totalWeightKg = shipment.weightAllPackages().kilograms();
        Price costByWeight = weightPriceProvider
                .costPerKg()
                .multiply(totalWeightKg);

        BigDecimal totalVolumeM3 = shipment.volumeAllPackages();
        Price costByVolume = volumePriceProvider
                .costPerCubicMeter()
                .multiply(totalVolumeM3);

        Price baseCost = costByWeight.max(costByVolume);
        Price minimalPrice = weightPriceProvider.minimalPrice();

        if (shipment.route() != null) {
            BigDecimal distanceFactor = shipment.route().distanceFactor();

            BigDecimal baseAmount = baseCost.amount();
            BigDecimal newAmount = baseAmount.multiply(distanceFactor);
            newAmount = newAmount.setScale(2, RoundingMode.CEILING);

            Price costWithDistance = new Price(newAmount, baseCost.currency());

            return costWithDistance.max(minimalPrice);
        }

        return baseCost.max(minimalPrice);
    }

    public Price minimalPrice() {
        return weightPriceProvider.minimalPrice();
    }
}
