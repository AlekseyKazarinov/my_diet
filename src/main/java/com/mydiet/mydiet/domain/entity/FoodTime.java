package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FoodTime {

    BREAKFAST("Завтрак"),
    LUNCH("Перекус"),
    DINNER("Обед"),
    AFTERNOON_SNACK("Полдник"),
    SUPPER("Ужин"),
    NIGHT_SNACK("Ночной перекус");

    private String description;
}
