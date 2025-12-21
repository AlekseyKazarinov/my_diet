package com.mydiet.mydiet.domain.dto.output.android;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MealApp {

    public Long id;
    public String foodTime;
    public RecipeIdContainer recipe;
}
