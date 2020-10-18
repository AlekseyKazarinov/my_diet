package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.RecipeCreationInput;
import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.domain.entity.Recipe;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final IngredientService ingredientService;
    private final RecipeRepository recipeRepository;

    public Recipe createValidatedRecipe(RecipeCreationInput recipeCreationInput) {
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

        return saveRecipe(recipe);
    }

    private Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.findRecipeByName(recipe.getName())
                               .orElseGet(() -> recipeRepository.save(recipe));
    }

    public List<Recipe> findAllRecipes() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> findRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId);
    }

    public List<Recipe> findAllRecipesSortedBySimilarityInCalories(Integer kkal, Integer maxCount) {
        Utils.validateValueWithNameIsNonNegative(kkal, "kkal");
        Utils.validateValueWithNameIsNonNegative(maxCount, "maxCount");

        var countAbove = maxCount / 2;
        var countBelow = maxCount - countAbove;

        var recipePageBelow = recipeRepository.findAllByTotalKkalLessThanEqualOrderByTotalKkalDesc(
                kkal.doubleValue(), PageRequest.of(0, countBelow)
        );
        var recipePageAbove = recipeRepository.findAllByTotalKkalGreaterThanOrderByTotalKkalAsc(
                kkal.doubleValue(), PageRequest.of(0, countAbove)
        );

        var recipeList = new ArrayList<>(recipePageAbove.toList());
        recipeList.addAll(recipePageBelow.toList());

        Comparator<Recipe> recipeComparatorByKkal = (recipe1, recipe2) ->
                                                    {return Double.compare(Math.abs(recipe1.getTotalKkal() - kkal) ,
                                                                           Math.abs(recipe2.getTotalKkal() - kkal));};

        return recipeList.stream().sorted(recipeComparatorByKkal).collect(Collectors.toList());
    }

    private void validateRecipeCreationInput(RecipeCreationInput recipeCreationInput) {
        Utils.validateFieldIsSet(recipeCreationInput.getName(), "Name", recipeCreationInput);
        Utils.validateFieldIsSet(recipeCreationInput.getDescription(), "Description", recipeCreationInput);
        Utils.validateValueIsNonNegative(recipeCreationInput.getTotalKkal(), "TotalKkal", recipeCreationInput);
        Utils.validateValueIsNonNegative(recipeCreationInput.getTotalFats(), "TotalFats", recipeCreationInput);
        Utils.validateValueIsNonNegative(recipeCreationInput.getTotalProteins(), "TotalProteins", recipeCreationInput);
        Utils.validateValueIsNonNegative(recipeCreationInput.getTotalCarbohydrates(), "TotalCarbohydrates", recipeCreationInput);

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
