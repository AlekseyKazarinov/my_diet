package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.RecipeCreationInput;
import com.mydiet.mydiet.domain.entity.Image;
import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.domain.entity.Recipe;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final IngredientService ingredientService;
    private final RecipeRepository recipeRepository;
    private final ImageService imageService;

    @PersistenceContext
    private final EntityManager entityManager;

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

    public Recipe getRecipeOrElseThrow(Long recipeId) {
        return findRecipeById(recipeId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Recipe with id: %s does not exist", recipeId))
                );
    }

    public Recipe updateValidatedRecipe(Long recipeId, RecipeCreationInput recipeUpdateInput) {
        validateRecipeUpdateInput(recipeUpdateInput);

        var recipe = getRecipeOrElseThrow(recipeId);
        recipe.setName(recipeUpdateInput.getName());
        recipe.setDescription(recipeUpdateInput.getDescription());

        if (recipe.getImage() != null) {
            imageService.updateImage(recipe.getImage().getId(), recipeUpdateInput.getImage());
            recipe.setImage(imageService.updateImage(recipe.getImage().getId(), recipeUpdateInput.getImage()));
        }

        recipe.setTotalKkal(recipeUpdateInput.getTotalKkal());
        recipe.setTotalProteins(recipeUpdateInput.getTotalProteins());
        recipe.setTotalCarbohydrates(recipeUpdateInput.getTotalCarbohydrates());
        recipe.setTotalFats(recipeUpdateInput.getTotalFats());

        // replace all ingredients
        var ingredients = new ArrayList<Ingredient>();
        for (var ingredientInput : recipeUpdateInput.getIngredients()) {
            ingredients.add(ingredientService.createIngredient(ingredientInput));
        }
        recipe.setIngredients(ingredients);

        var originalIngredientIds = getOriginalIngredientsForRecipe(recipeId);
        recipe = recipeRepository.save(recipe);
        ingredientService.removeIngredientsWithIds(originalIngredientIds);
        return recipe;
    }

    @SuppressWarnings("unchecked")
    private List<Long> getOriginalIngredientsForRecipe(Long recipeId) {
        var findOriginalIngredientsForRecipe = "SELECT RI.INGREDIENT_ID FROM RECIPE_INGREDIENT RI"
                                        + " JOIN INGREDIENT I ON RI.INGREDIENT_ID = I.ID"
                                        + " JOIN RECIPE_INGREDIENT IR2 ON I.ID = IR2.INGREDIENT_ID"
                                        + " WHERE IR2.RECIPE_ID = ?"
                                        + " GROUP BY RI.INGREDIENT_ID"
                                        + " HAVING count(RI.RECIPE_ID) = 1";
        Query query = entityManager.createNativeQuery(findOriginalIngredientsForRecipe);
        query.setParameter(1, recipeId);

        var ingredientIds = new ArrayList<Long>();

        query.getResultList().forEach(id -> {
            ingredientIds.add(((Number) id).longValue());
        });
        log.info("original ingredients: {}", ingredientIds);
        // App is using Spring Data JPA instead of native queries:
        //var removeAllAssociationsForRecipe = "DELETE FROM RECIPE_INGREDIENT RI WHERE RI.RECIPE_ID = ?";
        //var removeAllIngredientsContaintedByRecipeAlone = "DELETE FROM INGREDIENT WHERE ID IN (:IDS)";
        return ingredientIds;

    }

    public Image addImageToRecipe(Long recipeId, String imageName, String imageSource) {
        var recipe = getRecipeOrElseThrow(recipeId);

        var image = imageService.createValidatedImage(imageName, imageSource);
        recipe.setImage(image);
        recipeRepository.save(recipe);

        return image;
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
        validateRecipeSpecificFields(recipeCreationInput);

        var recipeName = recipeCreationInput.getName();
        if (recipeRepository.findRecipeByName(recipeName).isPresent()) {
            var message = String.format("Recipe with Name: %s already exists", recipeName);

            throw new ValidationException(message);
        }

        validateIngredientsList(recipeCreationInput);

    }

    private void validateRecipeUpdateInput(RecipeCreationInput recipeUpdateInput) {
        validateRecipeSpecificFields(recipeUpdateInput);
        validateIngredientsList(recipeUpdateInput);
    }

    private void validateRecipeSpecificFields(RecipeCreationInput recipeInput) {
        Utils.validateFieldIsSet(recipeInput.getName(), "Name", recipeInput);
        Utils.validateFieldIsSet(recipeInput.getDescription(), "Description", recipeInput);
        Utils.validateValueIsNonNegative(recipeInput.getTotalKkal(), "TotalKkal", recipeInput);
        Utils.validateValueIsNonNegative(recipeInput.getTotalFats(), "TotalFats", recipeInput);
        Utils.validateValueIsNonNegative(recipeInput.getTotalProteins(), "TotalProteins", recipeInput);
        Utils.validateValueIsNonNegative(recipeInput.getTotalCarbohydrates(), "TotalCarbohydrates", recipeInput);

    }

    private void validateIngredientsList(RecipeCreationInput creationInput) {
        var ingredients = creationInput.getIngredients();

        if (ingredients == null || ingredients.isEmpty()) {
            var message = "List of ingredients should be set";
            throw new ValidationException(message);
        }

        for (var ingredient : ingredients) {
            ingredientService.validateIngredientCreationInput(ingredient);
        }
    }

}
