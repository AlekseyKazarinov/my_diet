package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.FoodTime;
import lombok.Data;

@Data
public class MealInputShortened {

    private Long recipeId;
    private FoodTime foodTime;

}
