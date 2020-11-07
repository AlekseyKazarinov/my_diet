package com.mydiet.mydiet.domain.entity;

import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.infrastructure.Consistence;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mydiet.mydiet.infrastructure.Consistence.*;

@AllArgsConstructor
public enum QuantityUnit {

    KILOGRAM("Килограмм", "кг.", Set.of(SOLID)),
    GRAM("Грамм", "г.", Set.of(SOLID)),
    MILLILITER("Миллилитр", "мл.", Set.of(SOLID)),
    LITER("Литр", "л.", Set.of(LIQUID)),
    TEASPOON("Чайная ложка", "ч.л.", Set.of(SOLID, LIQUID)),
    HEAPED_TEASPOON("Чайная ложка с горкой", "ч.л. с горкой", Set.of(SOLID)),
    TABLESPOON("Столовая ложка", "ст.л.", Set.of(SOLID, LIQUID)),
    HEAPED_TABLESPOON("Столовая ложка c горкой", "ст.л. с горкой", Set.of(SOLID)),
    GLASS("Стакан", "ст.", Set.of(SOLID, LIQUID)),
    CUP("Чашка", "чашка", Set.of(LIQUID)),
    PINCH("Щепотка", "щеп.", Set.of(SOLID)),
    PIECE("Штука", "шт.", Set.of(SOLID)),
    DROP("Капля", "капля", Set.of(LIQUID)),
    BY_TASTE("По вкусу", "по вкусу", Set.of(NOT_DEFINED)),
    OPTIONAL("По желанию", "по желанию", Set.of(NOT_DEFINED)),
    NOT_USED(null, null, Set.of(NOT_DEFINED));

    private String description;
    private String abbreviation;
    @Getter
    private Set<Consistence> consistences;

    private static String NOT_EXIST = "Quantity Unit %s does not exist";

    private static Set<String> unitNames = Arrays.stream(QuantityUnit.values())
            .flatMap(unit -> Stream.of(unit.name(), unit.description, unit.abbreviation))
            .collect(Collectors.toSet());

    // todo: use custom Json(De)Serializer to convert from string and back
    public static QuantityUnit of(String unit) {
        for (var quantityUnit : QuantityUnit.values()) {

            if (quantityUnit.name().equalsIgnoreCase(unit)
                    ||quantityUnit.description.equals(unit)
                    || quantityUnit.abbreviation.equals(unit)) {

                return quantityUnit;
            }
        }

        throw new IllegalArgumentException(String.format(NOT_EXIST, unit));
    }

    private static boolean isValidUnit(String name) {
        return unitNames.contains(name);
    }

    public static void validateUnit(String name) {
        if (!isValidUnit(name)) {
            var message = String.format("%s is not a valid QuantityUnit", name);
            throw new ValidationException(message);
        }
    }

    public static Set<QuantityUnit> getUnitsForConsistence(Consistence consistence) {
        return Arrays.stream(QuantityUnit.values())
                .filter(unit -> unit.consistences.contains(consistence))
                .collect(Collectors.toUnmodifiableSet());
    }

}
