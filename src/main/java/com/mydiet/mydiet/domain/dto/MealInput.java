package com.mydiet.mydiet.domain.dto;

import lombok.Data;

@Data
public class MealInput {

    private RecipeInput recipeInput;
    private String      foodTime;

}
