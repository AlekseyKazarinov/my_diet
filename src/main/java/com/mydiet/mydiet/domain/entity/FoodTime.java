package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FoodTime {

    BREAKFAST("Завтрак"),
    SNACK("Перекус"),
    DINNER("Обед"),
    AFTERNOON_SNACK("Полдник"),
    SUPPER("Ужин");

    private String description;
}
