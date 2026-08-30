package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.input.ImageInput;
import com.mydiet.mydiet.domain.dto.input.IngredientInput;
import com.mydiet.mydiet.domain.dto.input.ProductInput;
import com.mydiet.mydiet.domain.dto.input.RecipeInput;
import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.infrastructure.Consistence;
import com.mydiet.mydiet.infrastructure.UnitGraphService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static com.mydiet.mydiet.domain.entity.Lifestyle.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor
public class RecipeServiceTest {

    private final Long TEST_INGREDIENT_ID = 1234L;
    private final String TEST_PRODUCT_NAME = "TEST_PRODUCT";
    private final String TEST_RECIPE_NAME = "Простой салат из огурцов и помидоров";
    private final String TEST_RECIPE_DESCRIPTION = "Нарезать огурцы с помидорами в равных долях. " +
            "Заправить майонезом.";


    //@MockitoBean
    @Mock
    private IngredientService ingredientService;

    @Mock
    private RecipeStorageService recipeStorageService;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    public void createRecipeWithRecipeInput() {
        // Given
        var recipeInput = createRecipeInput();
        when(ingredientService.createIngredient(any())).thenReturn(createTestIngredient());
        when(recipeStorageService.saveIfOriginal(any())).then(AdditionalAnswers.returnsFirstArg());

        // When
        var recipe = recipeService.createRecipe(recipeInput);

        // Then
        Assertions.assertEquals(TEST_RECIPE_NAME, recipe.getName());
        Assertions.assertEquals(TEST_RECIPE_DESCRIPTION, recipe.getDescription());

        Assertions.assertEquals(2, recipe.getIngredients().size());
    }

    private Ingredient createTestIngredient() {
        var quantity = Quantity.of(2.0, QuantityUnit.KILOGRAM);

        return Ingredient.builder()
                .id(TEST_INGREDIENT_ID)
                .product(Product.builder()
                        .name(TEST_PRODUCT_NAME)
                        .language(Language.RUSSIAN)
                        .consistence(Consistence.SOLID)
                        .productType(ProductType.OTHER)
                        .build())
                .quantity(quantity)
                .build();
    }


    private RecipeInput createRecipeInput() {
        return RecipeInput.builder()
                .name(TEST_RECIPE_NAME)
                .foodCategory(FoodCategory.SALAD)
                .language(Language.RUSSIAN)
                .lifestyles(Set.of(VEGAN,VEGETARIAN,NOT_SPECIFIED))
                .description(TEST_RECIPE_DESCRIPTION)
                .ingredients(List.of(
                        createIngredientInputTomato(),
                        createIngredientInputCucumber()
                ))
                .image(createImageInput())

                .build();
    }

    private IngredientInput createIngredientInputTomato() {
        return IngredientInput.builder()
                .product(createProductInputTomato())
                .totalQuantity(0.5)
                .unit(QuantityUnit.KILOGRAM)
                .build();
    }

    private IngredientInput createIngredientInputCucumber() {
        return IngredientInput.builder()
                .product(createProductInputCucumber())
                .totalQuantity(0.5)
                .unit(QuantityUnit.KILOGRAM)
                .build();
    }

    private ProductInput createProductInputTomato() {
        return ProductInput.builder()
                .language(Language.RUSSIAN)
                .name("Помидор")
                .productType(ProductType.VEGETABLE)
                .consistence(Consistence.SOLID)
                .build();
    }

    private ProductInput createProductInputCucumber() {
        return ProductInput.builder()
                .language(Language.RUSSIAN)
                .name("Огурец")
                .productType(ProductType.VEGETABLE)
                .consistence(Consistence.SOLID)
                .build();
    }

    private ImageInput createImageInput() {
        return ImageInput.builder()
                .name("TestImageSalad")
                .resource("encrypted image")
                .build();
    }


}
