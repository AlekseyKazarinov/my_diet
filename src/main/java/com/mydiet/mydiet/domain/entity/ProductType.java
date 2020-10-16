package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum ProductType {

    FRUIT("Фрукт", 1),
    VEGETABLE("Овощ",1),
    FISH("Рыба",2),
    MEAT("Мясо",2),
    DAIRY("Молочный продукт",3),
    GROCERY("Бакалея",4),
    OTHER("Разное",5);

    private String description;
    private Integer number;

    private static Map<String, Integer> productTypes = Arrays.stream(ProductType.values())
                                                        .collect(Collectors.toMap(ProductType::getDescription,
                                                                                  ProductType::getNumber));

    public static Integer mapToNumber(String description) {
        if (!productTypes.containsKey(description)) {
            var message = String.format("Product Type %s does not exist", description);

            throw new IllegalArgumentException(message);
        }

        return productTypes.get(description);
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
