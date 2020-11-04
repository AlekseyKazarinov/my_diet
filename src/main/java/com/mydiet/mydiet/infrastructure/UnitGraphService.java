package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.Quantity;
import com.mydiet.mydiet.domain.entity.QuantityUnit;
import com.mydiet.mydiet.domain.exception.GenericException;
import com.mydiet.mydiet.repository.UnitConversionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.*;

import static com.mydiet.mydiet.domain.entity.QuantityUnit.*;
import static com.mydiet.mydiet.infrastructure.Consistence.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnitGraphService {

    public static final Set<QuantityUnit> CONVERTIBLE_UNITS = Set.of(
            TEASPOON, TABLESPOON, GLASS, CUP, PINCH, PIECE, DROP
    );

    private final UnitConversionRepository unitConversionRepository;

    public Quantity sum(Product product, Quantity... quantities) {
        var maxUnit = Collections.max(List.of(quantities), Comparator.comparing(Quantity::getUnit,
                (u1, u2) -> {return UnitGraph.compare(u1, u2, product.getConsistence());})).getUnit();

        if (!UnitGraph.isInShoppingList(maxUnit)) {
            maxUnit = UnitGraph.transformToClosestUnitInShoppingList(maxUnit, product.getConsistence());
        }

        var sumTotalQuantity = 0.0;

        for (var quantity : quantities) {
            sumTotalQuantity += transformTo(maxUnit, quantity, product).getTotalQuantity();
        }

        return new Quantity(sumTotalQuantity, maxUnit);
    }

    private Quantity transformTo(QuantityUnit resultUnit, Quantity quantity, Product product) {
        if (quantity.getUnit() == resultUnit) {
            return quantity;
        }

        var initUnit = quantity.getUnit();
        var totalCoef = 1.0;

        var nextStableUnit = UnitGraph.transformToClosestUnitInShoppingList(initUnit, product.getConsistence());
        totalCoef *= getCoefficientFor(initUnit, product.getId());

        while(nextStableUnit != resultUnit) {
            nextStableUnit = UnitGraph.getNextStableUnit(nextStableUnit, product.getConsistence());
            totalCoef *= getCoefficientFor(nextStableUnit, product.getId());
        }

        quantity.setUnit(resultUnit);
        quantity.setTotalQuantity(quantity.getTotalQuantity() * totalCoef);

        return quantity;
    }

    private Double getCoefficientFor(QuantityUnit initUnit, Long productId) {
        if (!CONVERTIBLE_UNITS.contains(initUnit)) {
            return getPredefinedCoefficientFor(initUnit);
        }

        var convCoefList = unitConversionRepository.findConversionCoefficients(initUnit.name().toLowerCase(), productId);

        if (convCoefList.isEmpty()) {
            var message = String.format("Failed to convert from %s to next unit for Product %s. " +
                    "Conversion coefficient does not exist", initUnit, productId);
            log.error("Conversiont error: {}", message);

            throw new GenericException(message);
        }

        return convCoefList.get(0);
    }

    private Double getPredefinedCoefficientFor(QuantityUnit initUnit) {
        switch (initUnit) {
            case GRAM:
            case MILLILITER:
                return 0.001;
            case HEAPED_TABLESPOON:
            case HEAPED_TEASPOON:
                return 1.5;
            case KILOGRAM:
            case LITER:
                return 1.0;
            default:
                throw new IllegalArgumentException("Non supported initUnit " +
                        initUnit.name() + ": not existing predefined coefficient");
        }
    }


    @PostConstruct
    public static void testUnitGraph() {

        log.info("Test 1: GLASS of liquid product > {}", UnitGraph.transformToClosestUnitInShoppingList(GLASS, LIQUID).name());
        log.info("Test 2: GLASS of solid product > {}", UnitGraph.transformToClosestUnitInShoppingList(GLASS, SOLID).name());
        log.info("Test 3: TEASPOON of solid product > {}", UnitGraph.transformToClosestUnitInShoppingList(TEASPOON, SOLID).name());
        log.info("Test 4: TEASPOON of liquid product > {}", UnitGraph.transformToClosestUnitInShoppingList(TEASPOON, LIQUID).name());
        log.info("Test 5: HEAPED TABLESPOON of solid product > {}", UnitGraph.transformToClosestUnitInShoppingList(HEAPED_TABLESPOON, SOLID).name());
        log.info("Test 6: PIECE of solid product > {}", UnitGraph.transformToClosestUnitInShoppingList(PIECE, SOLID).name());
        log.info("Test 7: GRAM of solid product > {}", UnitGraph.getNextStableUnit(GRAM, SOLID).name());
    }

}
