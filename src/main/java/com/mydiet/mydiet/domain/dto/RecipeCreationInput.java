package com.mydiet.mydiet.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecipeCreationInput {

    private String name;
    private String description;

    private List<IngredientInput> ingredients;
    private ImageCreationInput    image;

    private Double totalKkal;
    private Double totalProteins;
    private Double totalFats;
    private Double totalCarbohydrates;

}
