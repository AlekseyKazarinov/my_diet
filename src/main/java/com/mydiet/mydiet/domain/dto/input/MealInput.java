package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.FoodTime;
import lombok.Data;

@Data
public class MealInput {

    private RecipeInput recipeInput;
    private FoodTime foodTime;

}
