package com.mydiet.mydiet.config;

import com.google.common.collect.Lists;
import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.infrastructure.Consistence;
import com.mydiet.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrepareDataRunner implements CommandLineRunner {

    private final DailyDietRepository  dailyDietRepository;
    private final MealRepository       mealRepository;
    private final RecipeRepository     recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductRepository    productRepository;

    @Override
    public void run(String... args) throws Exception {
        saveDailyDietWithMealIds();

        log.info("Data preparation finished.");
    }


    private void saveDailyDietWithMealIds() {
        var product = Product.builder()
                .productType(ProductType.FISH)
                .name("fish")
                .consistence(Consistence.SOLID)
                .build();
        product = productRepository.save(product);

        var ingredient = Ingredient.builder()
                .product(product)
                .totalQuantity(1.0)
                .unit(QuantityUnit.PIECE)
                .build();

        ingredient = ingredientRepository.save(ingredient);

        var recipe = Recipe.builder()
                .description("fish")
                .ingredients(Lists.newArrayList(ingredient))
                .totalCarbohydrates(12.0)
                .totalProteins(12.0)
                .totalFats(12.0)
                .name("fish")
                .totalKkal(100.0)
                .build();

        recipe = recipeRepository.save(recipe);

        var listOfMeals = new HashSet<Meal>();

        long size = 3;

        var foodTimeList = List.of(FoodTime.BREAKFAST, FoodTime.DINNER, FoodTime.SUPPER);

        for (long i = 1; i <= size; i++) {
            var meal = new Meal();
            //meal.setId(i);
            meal.setRecipe(recipe);
            meal.setFoodTime(foodTimeList.get((int)i - 1));
            meal = mealRepository.save(meal);
            listOfMeals.add(meal);
        }

        var meal = new Meal();
        meal.setRecipe(recipe);
        meal.setFoodTime(FoodTime.NIGHT_SNACK);
        mealRepository.save(meal);


        var dailyDiet = new DailyDiet();
        dailyDiet.setMeals(listOfMeals);
        dailyDiet.setName("fish diet");
        dailyDietRepository.save(dailyDiet);
    }
}
