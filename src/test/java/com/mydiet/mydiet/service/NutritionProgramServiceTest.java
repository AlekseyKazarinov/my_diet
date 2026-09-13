package com.mydiet.mydiet.service;

import com.google.common.collect.Lists;
import com.mydiet.mydiet.domain.dto.input.NutritionProgramInput;
import com.mydiet.mydiet.domain.dto.input.ProgramTranslationInput;
import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.infrastructure.Consistence;
import com.mydiet.mydiet.repository.NutritionProgramRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static com.mydiet.mydiet.domain.entity.Language.ENGLISH;
import static com.mydiet.mydiet.domain.entity.Language.RUSSIAN;
import static com.mydiet.mydiet.domain.entity.Lifestyle.NOT_SPECIFIED;
import static com.mydiet.mydiet.domain.entity.Status.DRAFT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class NutritionProgramServiceTest {

    private final String NUTRITION_PROGRAM_NAME = "Test Nutrition Program";
    private final String NUTRITION_PROGRAM_SHORT_DESCRIPTION = "short description";
    private final String NUTRITION_PROGRAM_DESCRIPTION = "full desctiption";
    private final String RECIPE_LANG_GROUP_ID = UUID.randomUUID().toString();

    private final Long TOTAL_NUMBER_OF_DAILY_DIETS = 7L;
    private final String DAILY_DIET_NAME = "DailyDiet sample";

    private final Long NUTRITION_PROGRAM_NUMBER = 1L;

    @Mock
    private DailyDietService dailyDietService;

    @Mock
    private NutritionProgramStorageService nutritionProgramStorageService;

    @Mock
    private NutritionProgramRepository nutritionProgramRepository;

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
        Assertions.assertEquals(DRAFT, nutritionProgram.getStatus());

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

    @Test
    public void translateNutritionProgram() {
        // Given
        var originalProgram = getOriginalNutritionProgram();

        var programTranslationInput = getProgramTranslationInput();

        when(nutritionProgramStorageService.getProgramOrElseThrow(eq(NUTRITION_PROGRAM_NUMBER)))
                .thenReturn(originalProgram);
        when(nutritionProgramRepository.findNutritionProgramByLangGroupIdAndLanguage(any(), any()))
                .thenReturn(Optional.empty());

        when(dailyDietService.createTranslatedDailyDiet(any(), any()))
                .thenReturn(getTranslatedDailyDiet());
        when(nutritionProgramStorageService.saveIfOriginal(any()))
                .then(AdditionalAnswers.returnsFirstArg());
        
        // When
        var translatedProgram = nutritionProgramService.translateValidatedNutritionProgram(NUTRITION_PROGRAM_NUMBER, programTranslationInput);

        //Then
        Assertions.assertNotNull(translatedProgram);

        Assertions.assertEquals(ENGLISH, translatedProgram.getLanguage());
        Assertions.assertEquals(originalProgram.getLangGroupId(), translatedProgram.getLangGroupId());

        Assertions.assertEquals(programTranslationInput.getName(), translatedProgram.getName());
        Assertions.assertEquals(programTranslationInput.getDescription(), translatedProgram.getDescription());
        Assertions.assertEquals(DRAFT, translatedProgram.getStatus());
    }

    private ProgramTranslationInput getProgramTranslationInput() {
        return ProgramTranslationInput.builder()
                .name("FISH")
                .shortDescription("no short desctiption")
                .description("One fish day")
                .additionalInfo("no additional info")
                .language(ENGLISH)
                .build();
    }

    private DailyDiet getTranslatedDailyDiet() {
        return DailyDiet.builder()
                .name("fish diet")
                .meals(Set.of(Meal.builder()
                        .recipe(Recipe.builder()
                                .id(2L)
                                .name("Fish recipe")
                                .description("Fish recipe description")
                                .langGroupId(RECIPE_LANG_GROUP_ID)
                                .language(ENGLISH)
                                .ingredients(Lists.newArrayList(getTranslatedIngredient()))
                                .totalCarbohydrates(12.0)
                                .totalProteins(12.0)
                                .totalFats(12.0)
                                .totalKcal(100.0)
                                .build()
                        )
                        .foodTime(FoodTime.NIGHT_SNACK)
                        .build()))
                .build();

    }

    private Ingredient getTranslatedIngredient() {
        var product = Product.builder()
                .productType(ProductType.FISH)
                .name("FISH")
                .consistence(Consistence.SOLID)
                .langGroupId(UUID.randomUUID().toString())
                .language(ENGLISH)
                .build();

        var quantity = Quantity.of(1.0, QuantityUnit.PIECE);

        return Ingredient.builder()
                .product(product)
                .quantity(quantity)
                .build();
    }

    private NutritionProgram getOriginalNutritionProgram() {
        var product = Product.builder()
                .productType(ProductType.FISH)
                .name("Рыба")
                .consistence(Consistence.SOLID)
                .build();

        var quantity = Quantity.of(1.0, QuantityUnit.PIECE);

        var ingredient = Ingredient.builder()
                .product(product)
                .quantity(quantity)
                .build();

        var recipe = Recipe.builder()
                .langGroupId(RECIPE_LANG_GROUP_ID)
                .description("Описание рецепта рыбы")
                .ingredients(Lists.newArrayList(ingredient))
                .totalCarbohydrates(12.0)
                .totalProteins(12.0)
                .totalFats(12.0)
                .name("Рецепт Рыбы")
                .totalKcal(100.0)
                .build();

        var listOfMeals = new HashSet<Meal>();

        long size = 3;

        var foodTimeList = List.of(FoodTime.BREAKFAST, FoodTime.DINNER, FoodTime.SUPPER);

        for (long i = 1; i <= size; i++) {
            var meal = new Meal();
            meal.setRecipe(recipe);
            meal.setFoodTime(foodTimeList.get((int)i - 1));
            listOfMeals.add(meal);
        }

        var meal = new Meal();
        meal.setRecipe(recipe);
        meal.setFoodTime(FoodTime.NIGHT_SNACK);

        var dailyDiet = new DailyDiet();
        dailyDiet.setMeals(listOfMeals);
        dailyDiet.setName("Рыбная диета однодневная");

        return NutritionProgram.builder()
                .number(NUTRITION_PROGRAM_NUMBER)
                .name("РЫБА")
                .description("Один рыбный день")
                .langGroupId(UUID.randomUUID().toString())
                .dailyDiets(List.of(dailyDiet))
                .lifestyles(Set.of(NOT_SPECIFIED))
                .status(Status.PUBLISHED)
                .build();
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
                .langGroupId(UUID.randomUUID().toString())
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
