package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FoodTime {

    BREAKFAST("Завтрак"),
    LUNCH("Перекус"),
    DINNER("Обед"),
    AFTERNOON_SNACK("Полдник"),
    SUPPER("Ужин"),
    NIGHT_SNACK("Ночной перекус");

    private final String description;

    private static final String NOT_EXIST = "Food Time %s does not exist";

    public static FoodTime of(String description) {
        for (var foodTime : FoodTime.values()) {

            if (foodTime.name().equalsIgnoreCase(description)
                    || foodTime.description.equalsIgnoreCase(description)) {

                return foodTime;
            }
        }
        throw new IllegalArgumentException(String.format(NOT_EXIST, description));
    }

}
