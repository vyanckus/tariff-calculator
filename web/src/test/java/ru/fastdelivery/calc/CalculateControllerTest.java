package ru.fastdelivery.calc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.fastdelivery.ControllerTest;
import ru.fastdelivery.domain.common.coordinates.Point;
import ru.fastdelivery.domain.common.coordinates.PointFactory;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.presentation.api.request.CalculatePackagesRequest;
import ru.fastdelivery.presentation.api.request.CargoPackage;
import ru.fastdelivery.presentation.api.request.PointRequest;
import ru.fastdelivery.presentation.api.response.CalculatePackagesResponse;
import ru.fastdelivery.usecase.TariffCalculateUseCase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Контроллер расчета стоимости доставки")
class CalculateControllerTest extends ControllerTest {

    private static final String CALCULATE_API = "/api/v1/calculate";
    private static final String RUB_CURRENCY = "RUB";

    @MockBean
    private TariffCalculateUseCase tariffCalculateUseCase;

    @MockBean
    private CurrencyFactory currencyFactory;

    @MockBean
    private PointFactory pointFactory;

    private Currency createRubCurrency() {
        return new CurrencyFactory(code -> true).create(RUB_CURRENCY);
    }

    @Test
    @DisplayName("Расчет стоимости с валидными данными и габаритами -> 200 OK")
    void whenValidInputWithDimensions_thenReturn200() {
        CargoPackage cargoPackage = new CargoPackage(
                BigInteger.valueOf(4564),
                BigInteger.valueOf(345),
                BigInteger.valueOf(589),
                BigInteger.valueOf(234)
        );

        CalculatePackagesRequest request = new CalculatePackagesRequest(
                List.of(cargoPackage),
                RUB_CURRENCY
        );

        Currency rubCurrency = createRubCurrency();
        when(currencyFactory.create(RUB_CURRENCY)).thenReturn(rubCurrency);

        Price calculatedPrice = new Price(BigDecimal.valueOf(1825.6), rubCurrency);
        Price minimalPrice = new Price(BigDecimal.valueOf(350), rubCurrency);

        when(tariffCalculateUseCase.calc(any())).thenReturn(calculatedPrice);
        when(tariffCalculateUseCase.minimalPrice()).thenReturn(minimalPrice);

        ResponseEntity<CalculatePackagesResponse> response =
                restTemplate.postForEntity(CALCULATE_API, request, CalculatePackagesResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Расчет стоимости без габаритов (обратная совместимость) -> 200 OK")
    void whenValidInputWithoutDimensions_thenReturn200() {
        CargoPackage cargoPackage = new CargoPackage(BigInteger.valueOf(4564));

        CalculatePackagesRequest request = new CalculatePackagesRequest(
                List.of(cargoPackage),
                RUB_CURRENCY
        );

        Currency rubCurrency = createRubCurrency();
        when(currencyFactory.create(RUB_CURRENCY)).thenReturn(rubCurrency);

        Price calculatedPrice = new Price(BigDecimal.valueOf(1825.6), rubCurrency);
        Price minimalPrice = new Price(BigDecimal.valueOf(350), rubCurrency);

        when(tariffCalculateUseCase.calc(any())).thenReturn(calculatedPrice);
        when(tariffCalculateUseCase.minimalPrice()).thenReturn(minimalPrice);

        ResponseEntity<CalculatePackagesResponse> response =
                restTemplate.postForEntity(CALCULATE_API, request, CalculatePackagesResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Расчет стоимости с маршрутом -> 200 OK")
    void whenValidRoute_thenReturn200() {
        CargoPackage cargoPackage = new CargoPackage(
                BigInteger.valueOf(4564),
                BigInteger.valueOf(345),
                BigInteger.valueOf(589),
                BigInteger.valueOf(234)
        );

        PointRequest departure = new PointRequest(
                BigDecimal.valueOf(55.446008),
                BigDecimal.valueOf(65.339151)
        );

        PointRequest destination = new PointRequest(
                BigDecimal.valueOf(73.398660),
                BigDecimal.valueOf(55.027532)
        );

        CalculatePackagesRequest request = new CalculatePackagesRequest(
                List.of(cargoPackage),
                RUB_CURRENCY,
                departure,
                destination
        );

        Currency rubCurrency = createRubCurrency();
        when(currencyFactory.create(RUB_CURRENCY)).thenReturn(rubCurrency);

        Point mockDeparture = new Point(
                BigDecimal.valueOf(55.446008),
                BigDecimal.valueOf(65.339151)
        );
        Point mockDestination = new Point(
                BigDecimal.valueOf(73.398660),
                BigDecimal.valueOf(55.027532)
        );

        when(pointFactory.create(
                BigDecimal.valueOf(55.446008),
                BigDecimal.valueOf(65.339151)
        )).thenReturn(mockDeparture);

        when(pointFactory.create(
                BigDecimal.valueOf(73.398660),
                BigDecimal.valueOf(55.027532)
        )).thenReturn(mockDestination);

        Price calculatedPrice = new Price(BigDecimal.valueOf(456.00), rubCurrency);
        Price minimalPrice = new Price(BigDecimal.valueOf(350), rubCurrency);

        when(tariffCalculateUseCase.calc(any())).thenReturn(calculatedPrice);
        when(tariffCalculateUseCase.minimalPrice()).thenReturn(minimalPrice);

        ResponseEntity<CalculatePackagesResponse> response =
                restTemplate.postForEntity(CALCULATE_API, request, CalculatePackagesResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Пустой список упаковок -> 400 BAD_REQUEST")
    void whenEmptyPackagesList_thenReturn400() {
        CalculatePackagesRequest request = new CalculatePackagesRequest(
                List.of(),
                RUB_CURRENCY
        );

        ResponseEntity<String> response =
                restTemplate.postForEntity(CALCULATE_API, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Координаты вне допустимых границ -> 400 BAD_REQUEST")
    void whenCoordinatesOutOfBounds_thenReturn400() {
        CargoPackage cargoPackage = new CargoPackage(BigInteger.valueOf(4564));

        PointRequest departure = new PointRequest(
                BigDecimal.valueOf(70),
                BigDecimal.valueOf(65.339151)
        );

        PointRequest destination = new PointRequest(
                BigDecimal.valueOf(73.398660),
                BigDecimal.valueOf(55.027532)
        );

        CalculatePackagesRequest request = new CalculatePackagesRequest(
                List.of(cargoPackage),
                RUB_CURRENCY,
                departure,
                destination
        );

        ResponseEntity<String> response =
                restTemplate.postForEntity(CALCULATE_API, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
