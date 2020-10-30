package com.mydiet.mydiet.domain.entity;

import com.mydiet.mydiet.domain.exception.ValidationException;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public enum QuantityUnit {

    KILOGRAM("Килограмм", "кг."),
    GRAM("Грамм", "г."),
    MILLILITER("Миллилитр", "мл."),
    LITER("Литр", "л."),
    TEASPOON("Чайная ложка", "ч.л."),
    HEAPED_TEASPOON("Чайная ложка с горкой", "ч.л. с горкой"),
    TABLESPOON("Столовая ложка", "ст.л."),
    HEAPED_TABLESPOON("Столовая ложка c горкой", "ст.л. с горкой"),
    GLASS("Стакан", "ст."),
    CUP("Чашка", "чашка"),
    PINCH("Щепотка", "щеп."),
    PIECE("Штука", "шт."),
    BY_TASTE("По вкусу", "по вкусу"),
    OPTIONAL("По желанию", "по желанию"),
    DROP("Капля", "капля"),
    NOT_USED(null, null);

    private String description;
    private String abbreviation;

    private static String NOT_EXIST = "Quantity Unit %s does not exist";

    private static Set<String> unitNames = Arrays.stream(QuantityUnit.values())
            .flatMap(unit -> Stream.of(unit.description, unit.abbreviation))
            .collect(Collectors.toSet());

    public static QuantityUnit of(String unit) {
        for (var quantityUnit : QuantityUnit.values()) {

            if (quantityUnit.description.equals(unit)
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

}
