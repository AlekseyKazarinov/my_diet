package com.mydiet.mydiet.domain.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class DailyDietInput {

    private String    name;
    private Set<Long> mealIds;

}
