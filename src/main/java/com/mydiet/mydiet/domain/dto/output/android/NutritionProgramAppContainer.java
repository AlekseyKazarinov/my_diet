package com.mydiet.mydiet.domain.dto.output.android;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * Nutrition Program entity in android application format
 */
@Data
@Builder
public class NutritionProgramAppContainer {

    public NutritionProgramApp nutritionProgram;
    public Set<DailyDietApp> dailyDiets;
    public Set<MealApp> meals;
    public Set<RecipeApp> recipes;
    public Set<ImageApp> images;

}
