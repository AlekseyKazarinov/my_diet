package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.input.NutritionProgramInput;
import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.infrastructure.Consistence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.mydiet.mydiet.domain.entity.Language.ENGLISH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class NutritionProgramServiceTest {

    private final String NUTRITION_PROGRAM_NAME = "Test Nutrition Program";
    private final String NUTRITION_PROGRAM_SHORT_DESCRIPTION = "short description";
    private final String NUTRITION_PROGRAM_DESCRIPTION = "full desctiption";

    private final Long TOTAL_NUMBER_OF_DAILY_DIETS = 7L;
    private final String DAILY_DIET_NAME = "DailyDiet sample";

    @Mock
    private DailyDietService dailyDietService;

    @Mock
    private NutritionProgramStorageService nutritionProgramStorageService;

    @InjectMocks
    private NutritionProgramService nutritionProgramService;

    @Test
    public void createNutritionProgramWithValidNutritionProgramInput() {
        // Given
        var nutritionProgramInput = getNutritionProgramInput();
        var dailyDietSample = getDailyDietSample();
        when(dailyDietService.getDailyDietOrElseThrow(any())).thenReturn(dailyDietSample);
        doNothing().when(dailyDietService).validateDailyDietInputContainsNumberOfMealsEqualTo(any(), any());
        when(nutritionProgramStorageService.saveIfOriginal(any())).then(AdditionalAnswers.returnsFirstArg());

        // When
        var nutritionProgram = nutritionProgramService.createValidatedNutritionProgram(nutritionProgramInput);

        // Then
        Assertions.assertNotNull(nutritionProgram);
        Assertions.assertEquals(NUTRITION_PROGRAM_NAME, nutritionProgram.getName());
        Assertions.assertEquals(NUTRITION_PROGRAM_SHORT_DESCRIPTION, nutritionProgram.getShortDescription());
        Assertions.assertEquals(NUTRITION_PROGRAM_DESCRIPTION, nutritionProgram.getDescription());
        Assertions.assertEquals(ENGLISH, nutritionProgram.getLanguage());
        Assertions.assertEquals(Status.DRAFT, nutritionProgram.getStatus());

        Assertions.assertNotNull(nutritionProgram.getDailyDiets());

        Assertions.assertEquals(TOTAL_NUMBER_OF_DAILY_DIETS, nutritionProgram.getDailyDiets().size());

        for (int i = 0; i < TOTAL_NUMBER_OF_DAILY_DIETS; i++) {
            var dailyDiet = nutritionProgram.getDailyDiets().get(i);
            Assertions.assertEquals(DAILY_DIET_NAME, dailyDiet.getName());

            var meals = dailyDiet.getMeals();
            var meal = meals.stream()
                    .filter(m -> m.getFoodTime() == FoodTime.BREAKFAST)
                    .findFirst()
                    .get();
            Assertions.assertNotNull(meal);
            Assertions.assertEquals(1L, meal.getRecipe().getId());
        }
    }


    private NutritionProgramInput getNutritionProgramInput() {
        var dailyDietIds = new ArrayList<Long>();
        for (long i = 1; i <= TOTAL_NUMBER_OF_DAILY_DIETS; i++) {
            dailyDietIds.add(i);
        }

        return NutritionProgramInput.builder()
                .name(NUTRITION_PROGRAM_NAME)
                .shortDescription(NUTRITION_PROGRAM_SHORT_DESCRIPTION)
                .description(NUTRITION_PROGRAM_DESCRIPTION)
                .additionalInfo("empty additional info")
                .image(null)
                .dayColor("#FF0000")    // red
                .mainColor("#00FF00")   // green
                .lightColor("#0000FF")  // blue
                .language(ENGLISH)
                .lifestyles(Set.of(Lifestyle.NOT_SPECIFIED))
                .dailyNumberOfMeals((short) 3)
                .dailyDietIds(dailyDietIds)
                .build();

    }

    private DailyDiet getDailyDietSample() {
        var mealSet = Set.of(getMealOne(), getMealTwo(), getMealThree());

        return DailyDiet.builder()
                .name(DAILY_DIET_NAME)
                .lifestyles(Collections.emptySet())
                .meals(mealSet)
                .lifestyles(Set.of(Lifestyle.NOT_SPECIFIED))
                .build();
    }

    private Meal getMealOne() {
        var recipe = getRecipeOne();

        return Meal.builder()
                .id(1L)
                .recipe(recipe)
                .foodTime(FoodTime.BREAKFAST)
                .build();
    }

    private Meal getMealTwo() {
        var recipe = getRecipeTwo();

        return Meal.builder()
                .id(2L)
                .recipe(recipe)
                .foodTime(FoodTime.DINNER)
                .build();
    }

    private Meal getMealThree() {
        var recipe = getRecipeThree();

        return Meal.builder()
                .id(3L)
                .recipe(recipe)
                .foodTime(FoodTime.SUPPER)
                .build();
    }

    private Recipe getRecipeOne() {
        var ingredients = getCommonIngredients();

        return Recipe.builder()
                .id(1L)
                .name("Recipe One")
                .description("description for Recipe One")
                .language(ENGLISH)
                .foodCategory(FoodCategory.SNACK)
                .lifestyles(Set.of(Lifestyle.NOT_SPECIFIED))
                .ingredients(ingredients)
                .totalKcal(100.0)
                .totalProteins(20.00)
                .totalFats(15.0)
                .totalCarbohydrates(30.0)
                .build();
    }

    private Recipe getRecipeTwo() {
        var ingredients = getCommonIngredients();

        return Recipe.builder()
                .id(2L)
                .name("Recipe Two")
                .description("description for Recipe Two")
                .language(ENGLISH)
                .foodCategory(FoodCategory.SNACK)
                .lifestyles(Set.of(Lifestyle.NOT_SPECIFIED))
                .ingredients(ingredients)
                .totalKcal(150.0)
                .totalProteins(21.00)
                .totalFats(12.0)
                .totalCarbohydrates(50.0)
                .build();

    }

    private Recipe getRecipeThree() {
        var ingredients = getCommonIngredients();

        return Recipe.builder()
                .id(3L)
                .name("Recipe Three")
                .description("description for Recipe Three")
                .language(ENGLISH)
                .foodCategory(FoodCategory.SNACK)
                .lifestyles(Set.of(Lifestyle.NOT_SPECIFIED))
                .ingredients(ingredients)
                .totalKcal(200.0)
                .totalProteins(26.00)
                .totalFats(10.0)
                .totalCarbohydrates(40.0)
                .build();
    }

    private List<Ingredient> getCommonIngredients() {  // common ingredients for three test recipes
        var product1 = Product.builder()
                .id(1L)
                .name("product one")
                .language(ENGLISH)
                .productType(ProductType.FRUIT)
                .consistence(Consistence.SOLID)
                .build();

        var product2 = Product.builder()
                .id(2L)
                .name("product two")
                .language(ENGLISH)
                .productType(ProductType.GROCERY)
                .consistence(Consistence.SOLID)
                .build();

        return List.of(
                Ingredient.builder()
                        .id(1L)
                        .product(product1)
                        .quantity(Quantity.of(100.0, QuantityUnit.GRAM))
                        .build(),
                Ingredient.builder()
                        .id(2L)
                        .product(product2)
                        .quantity(Quantity.of(200.0, QuantityUnit.GRAM))
                        .build()
        );
    }


}
