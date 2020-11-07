package com.mydiet.mydiet.domain.dto.input;

import lombok.Data;

import java.util.Set;

@Data
public class DailyDietInput {

    private String    name;
    private Set<Long> mealIds;

}
