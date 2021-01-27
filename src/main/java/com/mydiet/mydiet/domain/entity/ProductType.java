package com.mydiet.mydiet.domain.entity;

import com.mydiet.mydiet.domain.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum ProductType {

    FRUIT("Фрукт", 1),
    VEGETABLE("Овощ",1),
    FISH("Рыба",2),
    MEAT("Мясо",2),
    SEAFOOD("Морепродукты", 2),
    DAIRY("Молочный продукт",3),
    GROCERY("Бакалея",4),
    OTHER("Разное",5);

    private final String description;
    private final Integer number;

    private static final String NOT_EXIST = "Product Type %s does not exist";

    private static final Map<String, Integer> productTypes = Arrays.stream(ProductType.values())
                                                      .collect(Collectors.toMap(ProductType::getDescription,
                                                                                  ProductType::getNumber));

    private static final Set<String> productTypeNames = Arrays.stream(ProductType.values())
                                                      .flatMap(type -> Stream.of(type.name(), type.description))
                                                      .collect(Collectors.toSet());

    public static ProductType of(String description) {
        for (var productType : ProductType.values()) {

            if (productType.name().equalsIgnoreCase(description)
                || productType.description.equalsIgnoreCase(description)) {

                return productType;
            }
        }
        throw new IllegalArgumentException(String.format(NOT_EXIST, description));
    }

    public static ProductType of(Integer number) {
        for (var productType : ProductType.values()) {
            if (productType.number.equals(number)) {
                return productType;
            }
        }
        throw new IllegalArgumentException(String.format(NOT_EXIST, number));
    }

    public static Integer mapToNumber(String description) {
        if (!productTypes.containsKey(description)) {
            var message = String.format(NOT_EXIST, description);

            throw new IllegalArgumentException(message);
        }

        return productTypes.get(description);
    }

    private static boolean isValidDescription(String description) {
        return productTypeNames.contains(description);
    }

    public static void validateDescription(String description) {
        if (!isValidDescription(description)) {
            var message = String.format("%s is not a valid ProductType", description);

            throw new ValidationException(message);
        }
    }

    public boolean isVeganProduct(ProductType productType) {
        switch (productType) {
            case FRUIT:
            case VEGETABLE:
            case GROCERY:
            case OTHER:
                return true;
            case FISH:
            case MEAT:
            case DAIRY:
                return false;
            default:
                throw new IllegalArgumentException("Product type " + productType + " can not be resolved");
        }
    }
}
