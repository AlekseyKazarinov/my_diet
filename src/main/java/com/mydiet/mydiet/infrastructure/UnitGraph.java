package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.QuantityUnit;
import com.mydiet.mydiet.domain.exception.GenericException;
import lombok.experimental.UtilityClass;

import java.util.*;

import static com.mydiet.mydiet.domain.entity.QuantityUnit.*;
import static com.mydiet.mydiet.infrastructure.Connection.connection;
import static com.mydiet.mydiet.infrastructure.Consistence.*;

@UtilityClass
public class UnitGraph {

    private static final Set<QuantityUnit> unitsForShoppingList = Set.of(
        KILOGRAM, GRAM, MILLILITER, LITER, PIECE, NOT_USED
    );

    public static boolean isInShoppingList(QuantityUnit unit) {
        return unitsForShoppingList.contains(unit);
    }

    private static final Map<QuantityUnit, Node> nodeMap = new HashMap<>();

    static {
        // solid state
        UnitGraph.create(connection()
                .forType(SOLID)
                .from(GLASS, TEASPOON, TABLESPOON, PIECE, PINCH)
                .to(GRAM)
        );
        UnitGraph.create(connection().forType(SOLID).from(HEAPED_TABLESPOON).to(TABLESPOON));
        UnitGraph.create(connection().forType(SOLID).from(HEAPED_TEASPOON).to(TEASPOON));
        UnitGraph.create(connection().forType(SOLID).from(GRAM).to(KILOGRAM));

        // liquid state
        UnitGraph.create(connection()
                .forType(LIQUID)
                .from(GLASS, TEASPOON, CUP, DROP)
                .to(MILLILITER)
        );
        UnitGraph.create(connection().forType(LIQUID).from(MILLILITER).to(LITER));

        // not defined state
        UnitGraph.create(connection().forType(NOT_DEFINED).from(BY_TASTE).to(NOT_USED));
    }

    private static void create(Connection connection) {
        createConnection(connection.consistence(), connection.toUnit(), connection.fromUnits());
    }

    private static void createConnection(Consistence forConsistence, QuantityUnit toUnit, QuantityUnit... fromUnits) {
        for (var fromUnit : fromUnits) {
            directLine(forConsistence, fromUnit, toUnit);
        }
    }

    private static void directLine(Consistence forType, QuantityUnit from, QuantityUnit to) {
        var startNode = nodeMap.containsKey(from) ? nodeMap.get(from) : Node.of(from);
        var endNode = nodeMap.containsKey(to) ? nodeMap.get(to) : Node.of(to);

        startNode.setNextNode(endNode, forType);

        nodeMap.put(from, startNode);
        nodeMap.put(to, endNode);
    }

    public static QuantityUnit transformToClosestUnitInShoppingList(QuantityUnit unit, Consistence consistence) {
        var node = nodeMap.get(unit);

        Node nextNode;
        while (!node.inShoppingList) {
            nextNode = node.getNextNode(consistence);

            if (nextNode == null) {
                break;
            }
            node = nextNode;
        }

        return node.unit;
    }

    public static QuantityUnit getNextStableUnit(QuantityUnit unit, Consistence consistence) {
        var node = nodeMap.get(unit);

        if (node.inShoppingList && node.getNextNode(consistence)!= null ) {
            return node.getNextNode(consistence).unit;
        }

        while (!node.inShoppingList) {
            node = node.getNextNode(consistence);
        }

        return node.unit;
    }

    public static int compare(QuantityUnit unit1, QuantityUnit unit2, Consistence consistence) {
        if (unit1 == unit2) {
            return 0;
        }

        var node1 = nodeMap.get(unit1);
        var node2 = nodeMap.get(unit2);

        var firstIsAchievableFromSecond = node1.isAchievableFrom(node2, consistence);
        var secondIsAchievableFromFirst = node2.isAchievableFrom(node1, consistence);

        if (firstIsAchievableFromSecond && !secondIsAchievableFromFirst) {
            return 1;
        }

        if (secondIsAchievableFromFirst && !firstIsAchievableFromSecond) {
            return -1;
        }

        throw new GenericException("Failed to compare units due to inconsistent configuration of UnitGraph");
    }


    private static class Node {

        private final QuantityUnit unit;
        private final boolean      inShoppingList;

        Map<Consistence, Node> consistenceNodeMap = new HashMap<>();

        public static Node of(QuantityUnit unit) {
            return new Node(unit);
        }

        private Node(QuantityUnit unit) {
            this.unit = unit;
            this.inShoppingList = isInShoppingList(unit);
        }

        Node getNextNode(Consistence consistence) {
            if (consistenceNodeMap.size() == 1) {
                return consistenceNodeMap.entrySet().iterator().next().getValue();
            }

            return consistenceNodeMap.getOrDefault(consistence, null);
        }

        void setNextNode(Node nextNode, Consistence consistence) {
            consistenceNodeMap.put(consistence, nextNode);
        }

        public boolean isAchievableFrom(Node node, Consistence consistence) {
            if (node == this) {
                return true;
            }

            var nextNode = node.getNextNode(consistence);

            while (nextNode != null) {
                if (nextNode == this) {
                    return true;
                }
                nextNode = node.getNextNode(consistence);
            }

            return false;
        }
    }
}
