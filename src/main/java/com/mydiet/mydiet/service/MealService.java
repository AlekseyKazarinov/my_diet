package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.input.MealInput;
import com.mydiet.mydiet.domain.entity.FoodTime;
import com.mydiet.mydiet.domain.entity.Meal;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealService {

    private final RecipeService recipeService;
    private final MealRepository mealRepository;

    public Meal createValidatedMeal(MealInput mealCreationInput) {
        validateMealInput(mealCreationInput);
        return createMeal(mealCreationInput);
    }

    private void validateMealInput(MealInput mealInput) {
        Utils.validateStringFieldIsSet(mealInput.getFoodTime(), "FoodTime", mealInput);
        recipeService.validateRecipeInput(mealInput.getRecipeInput());
    }

    public Meal createMeal(MealInput mealCreationInput) {
        var recipe = recipeService.createRecipe(mealCreationInput.getRecipeInput());

        var meal = Meal.builder()
                .recipe(recipe)
                .foodTime(FoodTime.of(mealCreationInput.getFoodTime()))
                .build();

        return saveIfOriginal(meal);
    }

    public Meal saveIfOriginal(Meal meal) {
        var optionalStoredMeal = mealRepository.findOne(Example.of(meal));

        if (optionalStoredMeal.isPresent()) {
            log.info("Meal: {} at {} is already exist", meal.getRecipe().getName(), meal.getFoodTime());
            return optionalStoredMeal.get();
        }

        return mealRepository.save(meal);
    }

    public Optional<Meal> findMealById(Long mealId) {
        return mealRepository.findById(mealId);
    }

    public List<Meal> findAllMeals() {
        return mealRepository.findAll();
    }

    public Meal getMealOrElseThrow(Long mealId) {
        return findMealById(mealId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Meal with id: %s does not exist", mealId))
                );
    }

    public Meal updateValidatedMeal(Long mealId, MealInput mealUpdateInput) {
        validateMealInput(mealUpdateInput);
        return updateMeal(mealId, mealUpdateInput);
    }

    public Meal updateMeal(Long mealId, MealInput mealUpdateInput) {
        var meal = getMealOrElseThrow(mealId);

        if (mealUpdateInput.getRecipeInput() != null) {
            var updatedRecipe = recipeService.createRecipe(mealUpdateInput.getRecipeInput());
            meal.setRecipe(updatedRecipe);
        }

        meal.setFoodTime(FoodTime.of(mealUpdateInput.getFoodTime()));
        return mealRepository.save(meal);
    }

    public Meal setRecipeForMeal(Long mealId, Long recipeId) {
        var meal = getMealOrElseThrow(mealId);
        var recipe = recipeService.getRecipeOrElseThrow(recipeId);

        meal.setRecipe(recipe);
        return mealRepository.save(meal);
    }

    public Meal setFoodTimeForMeal(Long mealId, String foodTime) {
        Utils.validateTextVariableIsSet(foodTime, "foodTime");

        var meal = getMealOrElseThrow(mealId);
        meal.setFoodTime(FoodTime.of(foodTime));

        return mealRepository.save(meal);
    }

    public List<Meal> getMealsByFoodTime(String foodTime) {
        Utils.validateTextVariableIsSet(foodTime, "foodTime");

        return mealRepository.findMealsByFoodTime(FoodTime.of(foodTime));
    }
}
