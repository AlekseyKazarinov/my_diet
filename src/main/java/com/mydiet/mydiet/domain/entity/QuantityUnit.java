package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum QuantityUnit {

    GRAM("Грамм", "г."),
    TEASPOON("Чайная ложка", "ч.л."),
    HEAPED_TEASPOON("Чайная ложка с горкой", "ч.л. с горкой"),
    TABLESPOON("Столовая ложка", "ст.л."),
    HEAPED_TABLESPOON("Столовая ложка c горкой", "ст.л. с горкой"),
    GLASS("Стакан", "ст."),
    PINCH("Щепотка", "щеп."),
    PIECE("Штука", "шт.");

    private String name;
    private String abbreviation;

}
