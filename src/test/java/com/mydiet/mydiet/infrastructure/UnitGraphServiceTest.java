package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.ProductType;
import com.mydiet.mydiet.domain.entity.Quantity;
import com.mydiet.mydiet.domain.entity.QuantityUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;


@Slf4j
@SpringBootTest
@RequiredArgsConstructor
class UnitGraphServiceTest {

    private final Long PRODUCT_ID = 1234L;

    private static final Map<QuantityUnit, Double> conversionCoefs = Map.of(QuantityUnit.PIECE, 100.0, QuantityUnit.GRAM, 0.001);

    @MockBean
    private ConversionUnitsService conversionUnitsService;

    @Autowired
    @InjectMocks
    private UnitGraphService unitGraphService;

    @Test
    public void test() {

        var product = Product.builder()
                    .id(PRODUCT_ID)
                    .name("Помидоры")
                    .productType(ProductType.VEGETABLE)
                    .consistence(Consistence.SOLID)
                .build();

        var quantity1 = Quantity.of(2.0, QuantityUnit.PIECE);
        var quantity2 = Quantity.of(150.0, QuantityUnit.GRAM);

        when(conversionUnitsService.getCoefficientFor(QuantityUnit.PIECE, PRODUCT_ID)).thenReturn(100.0);
        when(conversionUnitsService.getCoefficientFor(QuantityUnit.GRAM, PRODUCT_ID)).thenReturn(0.001);

        var result = unitGraphService.sum(product, List.of(quantity1, quantity2));

        Assertions.assertEquals(QuantityUnit.GRAM, result.getUnit());
        Assertions.assertEquals(350.0, result.getTotalQuantity().doubleValue());
    }


}