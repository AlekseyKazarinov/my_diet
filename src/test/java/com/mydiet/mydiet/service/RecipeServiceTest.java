package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.input.*;
import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.infrastructure.Consistence;
import com.mydiet.mydiet.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.mydiet.mydiet.domain.entity.Lifestyle.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor
public class RecipeServiceTest {

    private final String LANG_GROUP_ID = UUID.randomUUID().toString();
    private final Long TEST_INGREDIENT_ID = 1234L;
    private final String TEST_PRODUCT_NAME = "TEST_PRODUCT";
    private final String TEST_RECIPE_NAME = "Простой салат из огурцов и помидоров";
    private final String TEST_RECIPE_DESCRIPTION = "Нарезать огурцы с помидорами в равных долях. " +
            "Заправить майонезом.";
    private final String ENGLISH_NAME = "Fish";
    private final String ENGLISH_DESCRIPTION = "Description of Fish";


    //@MockitoBean
    @Mock
    private IngredientService ingredientService;

    @Mock
    private RecipeStorageService recipeStorageService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    public void createRecipeWithRecipeInput() {
        // Given
        var recipeInput = createRecipeInput();
        when(ingredientService.createIngredient(any())).thenReturn(createTestProductIngredient());
        when(recipeStorageService.saveIfOriginal(any())).then(AdditionalAnswers.returnsFirstArg());

        // When
        var recipe = recipeService.createRecipe(recipeInput);

        // Then
        Assertions.assertEquals(TEST_RECIPE_NAME, recipe.getName());
        Assertions.assertEquals(TEST_RECIPE_DESCRIPTION, recipe.getDescription());

        Assertions.assertEquals(2, recipe.getIngredients().size());
    }

    @Test
    public void translateRecipeCorrect() {
        // Given
        when(recipeRepository.findById(eq(1L))).thenReturn(Optional.of(createOriginalRecipe()));
        when(recipeRepository.save(any())).then(AdditionalAnswers.returnsFirstArg());
        when(recipeStorageService.saveIfOriginal(any())).then(AdditionalAnswers.returnsFirstArg());

        var recipeTranslationInput = createEnglishRecipeTranslationInput();
        var translatedProduct = getTranslatedProduct();

        doReturn(translatedProduct).when(productService).getProductByLangGroupIdOrThrow(anyString(), any());

        // When
        var translatedRecipe = recipeService.translateValidatedRecipe(1L, recipeTranslationInput);

        // Then
        Assertions.assertEquals(Language.ENGLISH, translatedRecipe.getLanguage());
        Assertions.assertNotNull(translatedRecipe.getLangGroupId());

        Assertions.assertEquals(ENGLISH_NAME, translatedRecipe.getName());
        Assertions.assertEquals(ENGLISH_DESCRIPTION, translatedRecipe.getDescription());
    }

    private RecipeTranslationInput createEnglishRecipeTranslationInput() {
        return RecipeTranslationInput.builder()
                .name(ENGLISH_NAME)
                .description(ENGLISH_DESCRIPTION)
                .language(Language.ENGLISH)
                .build();
    }

    private Recipe createOriginalRecipe() {
        return Recipe.builder()
                .id(1L)
                .name("Рыба")
                .description("Описание рыбы")
                .langGroupId(UUID.randomUUID().toString())
                .language(Language.RUSSIAN)
                .ingredients(List.of(createFishIngredient()))
                .totalKcal(100.0)
                .totalProteins(12.0)
                .totalFats(12.0)
                .totalCarbohydrates(12.0)
                .build();
    }

    private Ingredient createFishIngredient() {
        return Ingredient.builder()
                .id(1L)
                .product(Product.builder()
                        .id(1L)
                        .name("Рыба")
                        .langGroupId(LANG_GROUP_ID)
                        .productType(ProductType.FISH)
                        .consistence(Consistence.SOLID)
                        .build())
                .quantity(Quantity.of(1.0, QuantityUnit.PIECE))
                .build();
    }

    private Product getTranslatedProduct() {
        return Product.builder()
                .name("fish")
                .langGroupId(LANG_GROUP_ID)
                .language(Language.ENGLISH)
                .productType(ProductType.FISH)
                .consistence(Consistence.SOLID)
                .build();
    }

    private Ingredient createTestProductIngredient() {
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
