package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.RecipeCreationInput;
import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.domain.entity.Recipe;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final IngredientService ingredientService;
    private final RecipeRepository recipeRepository;

    public Recipe createRecipe(RecipeCreationInput recipeCreationInput) {
        validateRecipeCreationInput(recipeCreationInput);

        var ingredients = new ArrayList<Ingredient>();

        for (var ingredientInput : recipeCreationInput.getIngredients()) {
            ingredients.add(ingredientService.createIngredient(ingredientInput));
        }

        var recipe = Recipe.builder()
                .name(recipeCreationInput.getName())
                .description(recipeCreationInput.getDescription())
                .ingredients(ingredients)
                .totalKkal(recipeCreationInput.getTotalKkal())
                .totalProteins(recipeCreationInput.getTotalProteins())
                .totalFats(recipeCreationInput.getTotalFats())
                .totalCarbohydrates(recipeCreationInput.getTotalCarbohydrates())
                .build();

        return recipeRepository.save(recipe);
    }

    public Optional<Recipe> findRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId);
    }

    private void validateRecipeCreationInput(RecipeCreationInput recipeCreationInput) {
        Utils.validateFieldIsSet(recipeCreationInput.getName(), recipeCreationInput);
        Utils.validateFieldIsSet(recipeCreationInput.getDescription(), recipeCreationInput);
        Utils.validateValueIsSet(recipeCreationInput.getTotalKkal(), recipeCreationInput);
        Utils.validateValueIsSet(recipeCreationInput.getTotalFats(), recipeCreationInput);
        Utils.validateValueIsSet(recipeCreationInput.getTotalProteins(), recipeCreationInput);
        Utils.validateValueIsSet(recipeCreationInput.getTotalCarbohydrates(), recipeCreationInput);

        var ingredients = recipeCreationInput.getIngredients();

        if (ingredients == null || ingredients.isEmpty()) {
            var message = "List of ingredients should be set";
            throw new ValidationException(message);
        }

        for (var ingredient : ingredients) {
            ingredientService.validateIngredientCreationInput(ingredient);
        }
    }



}
