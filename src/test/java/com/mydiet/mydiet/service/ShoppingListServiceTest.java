package com.mydiet.mydiet.service;

import com.google.common.collect.Lists;
import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.infrastructure.Consistence;
import com.mydiet.mydiet.infrastructure.UnitGraphService;
import com.mydiet.mydiet.repository.ShoppingListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mydiet.mydiet.domain.entity.Lifestyle.NOT_SPECIFIED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor
public class ShoppingListServiceTest {

    private Long NUTRITION_PROGRAM_NUMBER = 1L;

    private Long NUMBER_OF_FISH_MEALS = 3L;

    private String RECIPE_DESCRIPTION = "fish";
    private ProductType PRODUCT_TYPE = ProductType.FISH;
    private String PRODUCT_NAME = "fish";

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private UnitGraphService unitGraphService;

    @InjectMocks
    private ShoppingListService shoppingListService;

    @Test
    public void generateShoppingListFromNutritionProgram() {
        // Given
        var nutritionProgram = createNutritionProgram();
        when(shoppingListRepository.save(any())).then(AdditionalAnswers.returnsFirstArg());
        when(unitGraphService.sum(
                any(), // any Product ("fish")
                eq(List.of(Quantity.of(1.0, QuantityUnit.PIECE),
                        Quantity.of(1.0, QuantityUnit.PIECE),
                        Quantity.of(1.0, QuantityUnit.PIECE)))))
                .thenReturn(Quantity.of(3.0 , QuantityUnit.PIECE));

        // When
        var shoppingList = shoppingListService.generateShoppingListFor(nutritionProgram);

        // Then
        Assertions.assertNotNull(shoppingList);
        Assertions.assertEquals(Set.of(PRODUCT_TYPE), shoppingList.getAllProductTypes());
        Assertions.assertEquals(Set.of(PRODUCT_NAME), shoppingList.getAllProductNames());
        Assertions.assertNotNull(shoppingList.getListsByWeek());
        Assertions.assertEquals(1, shoppingList.getListsByWeek().size());

        Assertions.assertEquals(1, shoppingList.getListsByWeek().getFirst().getNumberOfWeek());

        var productListsByType = shoppingList.getListsByWeek().getFirst().getProductListsByType();  // first week
        var productListForFishType = productListsByType.get(PRODUCT_TYPE);
        var productRows = productListForFishType.getProductRows();

        Assertions.assertNotNull(productRows);
        Assertions.assertEquals( 1, productRows.size());

        var fishProductRow = productRows.getFirst();
        Assertions.assertEquals(PRODUCT_NAME, fishProductRow.getProductName());
        Assertions.assertEquals(NUMBER_OF_FISH_MEALS.doubleValue(), fishProductRow.getTotalQuantity());
        Assertions.assertEquals(QuantityUnit.PIECE, fishProductRow.getUnit());
    }

    public Product createProduct() {
        return Product.builder()
                .productType(PRODUCT_TYPE)
                .name(PRODUCT_NAME)
                .consistence(Consistence.SOLID)
                .build();
    }

    public NutritionProgram createNutritionProgram() {
        var product = createProduct();
        var quantity = Quantity.of(1.0, QuantityUnit.PIECE);

        var ingredient = Ingredient.builder()
                .product(product)
                .quantity(quantity)
                .build();

        var recipe = Recipe.builder()
                .description(RECIPE_DESCRIPTION)
                .ingredients(Lists.newArrayList(ingredient))
                .totalCarbohydrates(12.0)
                .totalProteins(12.0)
                .totalFats(12.0)
                .name("fish")
                .totalKcal(100.0)
                .build();


        var listOfMeals = new HashSet<Meal>();

        long size = NUMBER_OF_FISH_MEALS;

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
        dailyDiet.setName("fish diet");

        return NutritionProgram.builder()
                .number(NUTRITION_PROGRAM_NUMBER)
                .name("FISH")
                .description("One day with fish")
                .dailyDiets(List.of(dailyDiet))
                .lifestyles(Set.of(NOT_SPECIFIED))
                .status(Status.PUBLISHED)
                .build();
    }


}
