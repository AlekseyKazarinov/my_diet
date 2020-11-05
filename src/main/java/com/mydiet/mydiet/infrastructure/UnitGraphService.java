package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.Quantity;
import com.mydiet.mydiet.domain.entity.QuantityUnit;
import com.mydiet.mydiet.domain.exception.GenericException;
import com.mydiet.mydiet.repository.ConversionUnitsRepository;
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

    public Quantity sum(Product product, Quantity... quantities) {
        log.debug("sum quantities {} for Product #{}", quantities, product.getName());

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

        log.debug("next stable unit for {} is {}", initUnit, nextStableUnit);

        totalCoef *= conversionUnitsService.getCoefficientFor(initUnit, product.getId());

        while(nextStableUnit != resultUnit) {
            nextStableUnit = UnitGraph.getNextStableUnit(nextStableUnit, product.getConsistence());
            totalCoef *= conversionUnitsService.getCoefficientFor(nextStableUnit, product.getId());
        }
        log.debug("total coefficient for conversion {} --> {} is {}", initUnit, resultUnit, totalCoef);

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
