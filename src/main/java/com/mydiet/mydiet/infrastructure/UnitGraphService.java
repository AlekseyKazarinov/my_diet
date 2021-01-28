package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.Quantity;
import com.mydiet.mydiet.domain.entity.QuantityUnit;
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

    private final ConversionUnitsService conversionUnitsService;

    public Quantity sum(Product product, List<Quantity> quantities) {
        log.info("sum quantities {} for Product #{}", quantities, product.getName());

        var maxUnit = Collections.max(quantities, Comparator.comparing(Quantity::getUnit,
                (u1, u2) -> UnitGraph.compare(u1, u2, product.getConsistence()))).getUnit();

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
        if (!UnitGraph.isInShoppingList(resultUnit)) {
            throw new IllegalArgumentException(
                    String.format("Result Unit %s for Shopping list is invalid. Product: %s", resultUnit, product.getName())
            );
        }

        if (quantity.getUnit() == resultUnit) {
            return quantity;
        }

        var initUnit = quantity.getUnit();
        var totalCoef = 1.0;

        var currentUnit = initUnit;

        while(currentUnit != resultUnit) {
            totalCoef *= conversionUnitsService.getCoefficientFor(currentUnit, product.getId());
            currentUnit = UnitGraph.getNextStableUnit(currentUnit, product.getConsistence());
        }

        quantity.setUnit(resultUnit);
        quantity.setTotalQuantity(quantity.getTotalQuantity() * totalCoef);

        return quantity;
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
