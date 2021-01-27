package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.dto.input.ImageCreationInput;
import com.mydiet.mydiet.domain.dto.input.IngredientInput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecipeInput {

    private String name;
    private String description;

    private List<IngredientInput> ingredients;
    private ImageCreationInput    image;

    private Double totalKcal;
    private Double totalProteins;
    private Double totalFats;
    private Double totalCarbohydrates;

}
