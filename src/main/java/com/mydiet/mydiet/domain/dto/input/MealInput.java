package com.mydiet.mydiet.domain.dto.input;

import lombok.Data;

@Data
public class MealInput {

    private RecipeInput recipeInput;
    private String      foodTime;

}
