package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.output.android.*;
import com.mydiet.mydiet.domain.entity.Lifestyle;
import com.mydiet.mydiet.domain.entity.NutritionProgram;
import com.mydiet.mydiet.domain.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mydiet.mydiet.domain.entity.Status.PUBLISHED;

@Slf4j
@Service
@RequiredArgsConstructor
public class NutritionProgramConverterService {

    private final NutritionProgramStorageService programStorageService;

    private NutritionProgramAppContainer convertToAppFormat(NutritionProgram nutritionProgram) {
        var images = new HashSet<ImageApp>();
        var recipes = new HashSet<RecipeApp>();
        var meals = new HashSet<MealApp>();
        var dailyDiets = new HashSet<DailyDietApp>();

        for (var dailyDiet : nutritionProgram.getDailyDiets()) {

            var mealsForDailyDiet = new ArrayList<MealApp>();

            for (var meal: dailyDiet.getMeals()) {

                images.add(ImageApp.from(meal.getRecipe().getImage()));

                var recipeOutput = combineIntoRecipeForApp(meal.getRecipe());
                recipes.add(recipeOutput);

                mealsForDailyDiet.add(MealApp.builder()
                        .id(meal.getId())
                        .foodTime(meal.getFoodTime().toString())  // todo: ask Anya
                        .recipe(new RecipeIdContainer(recipeOutput.getId()))
                        .build());
            }

            meals.addAll(mealsForDailyDiet);

            dailyDiets.add(DailyDietApp.builder()
                    .meals(mealsForDailyDiet.stream()
                            .map(meal -> new MealIdContainer(meal.getId()))
                            .collect(Collectors.toList()))
                    .id(dailyDiet.getId())
                    .name(dailyDiet.getName())
                    .build());
        }

        var programImage = nutritionProgram.getImage();
        images.add(ImageApp.builder()
                .id(programImage.getId())
                .resource(programImage.getResource())
                .name(programImage.getName())
                .build());

        return combineAllEntitiesIntoProgramForApp(nutritionProgram, dailyDiets, meals, recipes, images);
    }

    private RecipeApp combineIntoRecipeForApp(com.mydiet.mydiet.domain.entity.Recipe recipe) {
        var recipeImage = recipe.getImage();
        var ingredientsOutput = new ArrayList<Ingredient>();

        for (var ingredient : recipe.getIngredients()) {
            ingredientsOutput.add(
                    Ingredient.builder()
                            .product(ingredient.getProduct().getName())
                            .totalQuantity(ingredient.getTotalQuantity())
                            .unit(ingredient.getUnit().shortName(ingredient.getProduct().getLanguage()))
                            .build()
            );
        }

        return RecipeApp.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .image(new ImageIdContainer(recipeImage.getId()))
                .foodCategory(recipe.getFoodCategory())
                .ingredients(ingredientsOutput)
                .totalKkal(recipe.getTotalKcal())
                .totalCarbohydrates(recipe.getTotalFats())
                .totalProteins(recipe.getTotalProteins())
                .totalFats(recipe.getTotalFats())
                .build();
    }

    private NutritionProgramAppContainer combineAllEntitiesIntoProgramForApp(
            NutritionProgram nutritionProgram,
            Set<DailyDietApp> dailyDiets,
            Set<MealApp> meals,
            Set<RecipeApp> recipes,
            Set<ImageApp> images
    ) {
        return NutritionProgramAppContainer.builder()
                .nutritionProgram(NutritionProgramApp.builder()
                        .number(nutritionProgram.getNumber())
                        .lastModifiedAt(nutritionProgram.getLastModifiedAt().toString())
                        .version(nutritionProgram.getVersion())
                        .image(new ImageIdContainer(nutritionProgram.getImage().getId()))
                        .dayColor(nutritionProgram.getDayColor())
                        .mainColor(nutritionProgram.getMainColor())
                        .lightColor(nutritionProgram.getLightColor())
                        .langId(nutritionProgram.getLangId())
                        .language(nutritionProgram.getLanguage().toString())
                        .name(nutritionProgram.getName())
                        .description(nutritionProgram.getDescription())
                        .shortDescription(nutritionProgram.getShortDescription())
                        .additionalInfo(nutritionProgram.getAdditionalInfo())
                        .dailyDiets(dailyDiets.stream()
                                .map(dailyDiet -> new DailyDietIdContainer(dailyDiet.getId()))
                                .collect(Collectors.toList()))
                        .dailyNumberOfMeals(nutritionProgram.getDailyNumberOfMeals())
                        .kcal(nutritionProgram.getKcal())
                        .lifestyles(nutritionProgram.getLifestyles().stream()
                                .map(Lifestyle::toString)
                                .collect(Collectors.toSet()))

                        .build())
                .dailyDiets(dailyDiets)
                .meals(meals)
                .recipes(recipes)
                .images(images)
                .build();
    }

    public NutritionProgramAppContainer getProgramConvertedIntoAppOutputFormat(Long programNumber) {
        var program = getProgramForAppUser(programNumber);
        return convertToAppFormat(program);
    }

    private NutritionProgram getProgramForAppUser(Long programNumber) {
        var program = programStorageService.getProgramOrElseThrow(programNumber);

        if (program.getStatus() != PUBLISHED) {
            log.warn("User tried to fetch NOT PUBLISHED Nutrition Program {}", program);

            throw new ForbiddenException(
                    String.format("You don't have access to the unpublished Nutrition Program %s", programNumber)
            );
        }

        return program;
    }

}
