package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Lifestyle {

    VEGETARIAN("вегетарианец"),
    VEGAN("веган"),
    DIABETIC("диабетик"),
    LACTOSE_INTOLERANCE("непереносящий лактозу");

    private final String description;

}
