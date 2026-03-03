package ru.fastdelivery.domain.delivery.pack;

import ru.fastdelivery.domain.common.weight.Weight;
import java.math.BigInteger;

/**
 * Упаковка груза
 *
 * @param weight     вес товаров в упаковке
 * @param dimensions габариты упаковки
 */
public record Pack(Weight weight, ru.fastdelivery.domain.delivery.pack.OuterDimensions dimensions) {

    private static final Weight MAX_WEIGHT = new Weight(BigInteger.valueOf(150_000));

    public Pack {
        if (weight.greaterThan(MAX_WEIGHT)) {
            throw new IllegalArgumentException("Package can't be more than " + MAX_WEIGHT);
        }
    }

    public Pack(Weight weight) {
        this(weight, null);
    }
}
