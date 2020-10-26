package com.mydiet.mydiet.domain.dto;

import com.mydiet.mydiet.domain.entity.DailyDiet;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NutritionProgramInput {

    private String name;
    private String description;

    private String image;
    private String backgroundColour;

    private List<DailyDietInput> dailyDietInputs;
    private Short numberOfMeals;

}
