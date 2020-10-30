package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.QuantityUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.mydiet.mydiet.infrastructure.Connection.connection;
import static com.mydiet.mydiet.infrastructure.Consistence.*;
import static com.mydiet.mydiet.domain.entity.QuantityUnit.*;

@Slf4j
@Component
public class UnitGraph {

    private static final Map<QuantityUnit, Node> nodeMap = new HashMap<>();

    private static final Set<QuantityUnit> unitsForShoppingList = Set.of(
            KILOGRAM, GRAM, MILLILITER, LITER, PIECE, NOT_USED
    );

    private static boolean IsInShoppingList(QuantityUnit unit) {
        return unitsForShoppingList.contains(unit);
    }

    private UnitGraph() {

        // solid state
        create(connection()
                .forType(SOLID)
                .from(GLASS, TEASPOON, TABLESPOON, PIECE, PINCH)
                .to(GRAM)
        );
        create(connection().forType(SOLID).from(HEAPED_TABLESPOON).to(TABLESPOON));
        create(connection().forType(SOLID).from(HEAPED_TEASPOON).to(TEASPOON));
        create(connection().forType(SOLID).from(GRAM).to(KILOGRAM));

        // liquid state
        create(connection()
                .forType(LIQUID)
                .from(GLASS, TEASPOON, CUP, DROP)
                .to(MILLILITER)
        );
        create(connection().forType(LIQUID).from(MILLILITER).to(LITER));

        // not defined state
        create(connection().forType(NOT_DEFINED).from(BY_TASTE).to(NOT_USED));

    }

    public static void create(Connection connection) {
        createConnection(connection.consistence(), connection.toUnit(), connection.fromUnits());
    }

    private static void createConnection(Consistence forConsistence, QuantityUnit toUnit, QuantityUnit... fromUnits) {
        for (var fromUnit : fromUnits) {
            directLine(forConsistence, fromUnit, toUnit);
        }
    }

    private static void directLine(Consistence forType, QuantityUnit from, QuantityUnit to) {
        var startNode = nodeMap.containsKey(from) ? nodeMap.get(from) : Node.of(from);//Node.of(from, forType);
        var endNode = nodeMap.containsKey(to) ? nodeMap.get(to) : Node.of(to); //Node.of(to, forType);

        //startNode.consistences.add(forType);
        //endNode.consistences.add(forType);

        startNode.setNextNode(endNode, forType);

        nodeMap.put(from, startNode);
        nodeMap.put(to, endNode);
    }

    @PostConstruct
    public static void testUnitGraph() {

        log.info("Test 1: GLASS of liquid product > {}", transform(GLASS, LIQUID).name());
        log.info("Test 2: GLASS of solid product > {}", transform(GLASS, SOLID).name());
        log.info("Test 3: TEASPOON of solid product > {}", transform(TEASPOON, SOLID).name());
        log.info("Test 4: TEASPOON of liquid product > {}", transform(TEASPOON, LIQUID).name());
        log.info("Test 5: HEAPED TABLESPOON of solid product > {}", transform(HEAPED_TABLESPOON, SOLID).name());
        log.info("Test 6: PIECE of solid product > {}", transform(PIECE, SOLID).name());
    }

    public static QuantityUnit transform(QuantityUnit unit, Consistence consistence) {
        var node = nodeMap.get(unit);

        while (!node.inShoppingList) {
            node = node.getNextNode(consistence);
        }

        return node.unit;
    }

    private static class Node {

        private final QuantityUnit     unit;
        private final boolean          inShoppingList;
        //private       Set<Consistence> consistences;

        Map<Consistence, Node> consistenceNodeMap = new HashMap<>();

        public static Node of(QuantityUnit unit) {//, Consistence... consistences) {
            /*if (consistences.length == 0) {
                return new Node(unit, IsInShoppingList(unit));

            } else {
                return new Node(unit, IsInShoppingList(unit), consistences);
            }*/
            return new Node(unit);
        }

        private Node(QuantityUnit unit) {//, boolean inShoppingList) {
            this.unit = unit;
            this.inShoppingList = IsInShoppingList(unit);
            //this.inShoppingList = inShoppingList;
        }

        /*private Node(QuantityUnit unit, boolean inShoppingList, Consistence... consistences) {
            this(unit, inShoppingList);
            //this.consistences = new HashSet<>(List.of(consistences));
        }*/

        Node getNextNode(Consistence consistence) {
            if (consistenceNodeMap.size() == 1) {
                return consistenceNodeMap.entrySet().iterator().next().getValue();
            }

            return consistenceNodeMap.getOrDefault(consistence, null);
        }

        void setNextNode(Node nextNode, Consistence consistence) {
            consistenceNodeMap.put(consistence, nextNode);
        }
    }
}
