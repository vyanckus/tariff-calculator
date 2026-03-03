package ru.fastdelivery.calc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.fastdelivery.ControllerTest;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.presentation.api.request.CalculatePackagesRequest;
import ru.fastdelivery.presentation.api.request.CargoPackage;
import ru.fastdelivery.presentation.api.response.CalculatePackagesResponse;
import ru.fastdelivery.usecase.TariffCalculateUseCase;
import ru.fastdelivery.usecase.VolumePriceProvider;
import ru.fastdelivery.usecase.WeightPriceProvider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CalculateControllerTest extends ControllerTest {

    final String baseCalculateApi = "/api/v1/calculate/";

    @MockBean
    TariffCalculateUseCase useCase;

    @MockBean
    CurrencyFactory currencyFactory;

    @MockBean
    WeightPriceProvider weightPriceProvider;

    @MockBean
    VolumePriceProvider volumePriceProvider;

    @Test
    @DisplayName("Валидные данные с габаритами -> Ответ 200")
    void whenValidInputWithDimensions_thenReturn200() {
        var request = new CalculatePackagesRequest(
                List.of(new CargoPackage(
                        BigInteger.TEN,
                        BigInteger.valueOf(345),
                        BigInteger.valueOf(589),
                        BigInteger.valueOf(234))),
                "RUB");

        var rub = new CurrencyFactory(code -> true).create("RUB");
        when(useCase.calc(any())).thenReturn(new Price(BigDecimal.valueOf(262.5), rub));
        when(useCase.minimalPrice()).thenReturn(new Price(BigDecimal.valueOf(5), rub));

        ResponseEntity<CalculatePackagesResponse> response =
                restTemplate.postForEntity(baseCalculateApi, request, CalculatePackagesResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Валидные данные без габаритов -> Ответ 200 (обратная совместимость)")
    void whenValidInputWithoutDimensions_thenReturn200() {
        var request = new CalculatePackagesRequest(
                List.of(new CargoPackage(BigInteger.TEN)),
                "RUB");

        var rub = new CurrencyFactory(code -> true).create("RUB");
        when(useCase.calc(any())).thenReturn(new Price(BigDecimal.valueOf(120), rub));
        when(useCase.minimalPrice()).thenReturn(new Price(BigDecimal.valueOf(5), rub));

        ResponseEntity<CalculatePackagesResponse> response =
                restTemplate.postForEntity(baseCalculateApi, request, CalculatePackagesResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Список упаковок == null -> Ответ 400")
    void whenEmptyListPackages_thenReturn400() {
        var request = new CalculatePackagesRequest(null, "RUB");

        ResponseEntity<String> response = restTemplate.postForEntity(baseCalculateApi, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
