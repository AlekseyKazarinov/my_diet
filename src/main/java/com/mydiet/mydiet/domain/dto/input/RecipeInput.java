package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.dto.input.ImageCreationInput;
import com.mydiet.mydiet.domain.dto.input.IngredientInput;
import com.mydiet.mydiet.domain.entity.FoodCategory;
import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.Lifestyle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecipeInput {

    private String         name;
    private String         description;
    private FoodCategory   foodCategory;
    private Set<Lifestyle> lifestyles;

    private Language language;

    private List<IngredientInput> ingredients;
    private ImageCreationInput    image;

    private Double totalKcal;
    private Double totalProteins;
    private Double totalFats;
    private Double totalCarbohydrates;

}
