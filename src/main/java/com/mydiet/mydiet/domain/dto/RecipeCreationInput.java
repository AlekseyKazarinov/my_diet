package com.mydiet.mydiet.domain.dto;

import com.mydiet.mydiet.domain.entity.Image;
import com.mydiet.mydiet.domain.entity.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecipeCreationInput {

    private String name;
    private String description;

    private List<IngredientCreationInput> ingredients;
    private Image                         image;

    private Double totalKkal;
    private Double totalProteins;
    private Double totalFats;
    private Double totalCarbohydrates;

}
