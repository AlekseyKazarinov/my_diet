package com.mydiet.mydiet.domain.dto.output.android;

import com.mydiet.mydiet.domain.entity.FoodCategory;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecipeApp {

    public Long id;

    public ImageIdContainer image;

    // text
    public String name;
    public String description;

    //
    public List<Ingredient> ingredients;
    private FoodCategory foodCategory;

    public Double totalCarbohydrates;
    public Double totalFats;
    public Double totalKkal;
    public Double totalProteins;

}
