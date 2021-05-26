package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.Quantity;
import com.mydiet.mydiet.domain.entity.QuantityUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.mydiet.mydiet.domain.entity.QuantityUnit.*;

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

        return Quantity.of(sumTotalQuantity, maxUnit);
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

}
