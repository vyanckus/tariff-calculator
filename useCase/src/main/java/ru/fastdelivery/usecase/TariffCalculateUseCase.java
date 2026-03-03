package ru.fastdelivery.usecase;

import lombok.RequiredArgsConstructor;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.delivery.shipment.Shipment;

import javax.inject.Named;

@Named
@RequiredArgsConstructor
public class TariffCalculateUseCase {

    private final WeightPriceProvider weightPriceProvider;
    private final VolumePriceProvider volumePriceProvider;

    public Price calc(Shipment shipment) {
        var totalWeightKg = shipment.weightAllPackages().kilograms();
        var costByWeight = weightPriceProvider
                .costPerKg()
                .multiply(totalWeightKg);

        var totalVolumeM3 = shipment.volumeAllPackages();
        var costByVolume = volumePriceProvider
                .costPerCubicMeter()
                .multiply(totalVolumeM3);

        var minimalPrice = weightPriceProvider.minimalPrice();

        return costByWeight
                .max(costByVolume)
                .max(minimalPrice);
    }

    public Price minimalPrice() {
        return weightPriceProvider.minimalPrice();
    }
}
