package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.QuantityUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.mydiet.mydiet.domain.entity.QuantityUnit.*;
import static com.mydiet.mydiet.infrastructure.Consistence.*;

@Slf4j
class UnitGraphTest {

    @Test
    public void glassOfLiquidProduct() {
        Assertions.assertEquals(MILLILITER, closestUnitInShoppingList(GLASS, LIQUID), "not expected unit");
        Assertions.assertEquals(GRAM, closestUnitInShoppingList(GLASS, SOLID));
        Assertions.assertEquals(GRAM, closestUnitInShoppingList(TEASPOON, SOLID));
        Assertions.assertEquals(MILLILITER, closestUnitInShoppingList(TEASPOON, LIQUID));
        Assertions.assertEquals(GRAM, closestUnitInShoppingList(HEAPED_TABLESPOON, SOLID));
        Assertions.assertEquals(PIECE, closestUnitInShoppingList(PIECE, SOLID));
        Assertions.assertEquals(KILOGRAM, nextStableUnit(GRAM, SOLID));
        Assertions.assertEquals(NOT_USED, closestUnitInShoppingList(OPTIONAL, NOT_DEFINED));
        Assertions.assertEquals(LITER, nextStableUnit(MILLILITER, LIQUID));
    }

    private QuantityUnit nextStableUnit(QuantityUnit unit, Consistence consistence) {
        return UnitGraph.getNextStableUnit(unit, consistence);
    }

    private QuantityUnit closestUnitInShoppingList(QuantityUnit unit, Consistence consistence) {
        return UnitGraph.transformToClosestUnitInShoppingList(unit, consistence);
    }

}