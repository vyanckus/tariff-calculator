package ru.fastdelivery.presentation.calc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.length.Length;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.OuterDimensions;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import ru.fastdelivery.presentation.api.request.CalculatePackagesRequest;
import ru.fastdelivery.presentation.api.request.CargoPackage;
import ru.fastdelivery.presentation.api.response.CalculatePackagesResponse;
import ru.fastdelivery.usecase.TariffCalculateUseCase;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calculate/")
@RequiredArgsConstructor
@Tag(name = "Расчеты стоимости доставки")
public class CalculateController {

    private final TariffCalculateUseCase tariffCalculateUseCase;
    private final CurrencyFactory currencyFactory;

    @PostMapping
    @Operation(summary = "Расчет стоимости по упаковкам груза")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    public CalculatePackagesResponse calculate(
            @Valid @RequestBody CalculatePackagesRequest request) {

        List<Pack> packs = request.packages().stream()
                .map(this::toPack)
                .toList();

        var shipment = new Shipment(packs, currencyFactory.create(request.currencyCode()));
        var calculatedPrice = tariffCalculateUseCase.calc(shipment);
        var minimalPrice = tariffCalculateUseCase.minimalPrice();

        return new CalculatePackagesResponse(calculatedPrice, minimalPrice);
    }

    private Pack toPack(CargoPackage cargo) {
        Weight weight = new Weight(cargo.weight());

        if (cargo.length() == null || cargo.width() == null || cargo.height() == null) {
            return new Pack(weight);
        }

        Length length = new Length(cargo.length());
        Length width = new Length(cargo.width());
        Length height = new Length(cargo.height());

        OuterDimensions dimensions = new OuterDimensions(length, width, height);
        return new Pack(weight, dimensions);
    }
}
