package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.domain.dto.input.MealInput;
import com.mydiet.mydiet.domain.dto.input.MealInputShortened;
import com.mydiet.mydiet.domain.entity.FoodTime;
import com.mydiet.mydiet.domain.entity.Meal;
import com.mydiet.mydiet.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
@Tag(name = "Meals") // Заменено с @Api(tags = ...)
public class MealController {

    private final MealService mealService;

    @PostMapping("/shortened-input")
    @Operation(summary = "Create a new Meal with RecipeId")
    public ResponseEntity<Meal> createMeal(@RequestBody MealInputShortened mealInputShortened) {
        var meal = mealService.createValidatedMealByShortenedInput(mealInputShortened);

        return ResponseEntity.status(HttpStatus.CREATED).body(meal);
    }

    @PostMapping
    @Operation(summary = "Create a new Meal with entire Recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meal created", content = @Content(schema = @Schema(implementation = Meal.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<Meal> createMeal(@RequestBody @NonNull MealInput mealCreationInput) {
        var meal = mealService.createValidatedMeal(mealCreationInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(meal);
    }

    @GetMapping("/{mealId}")
    @Operation(summary = "Get a Meal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meal received", content = @Content(schema = @Schema(implementation = Meal.class))),
            @ApiResponse(responseCode = "204", description = "Meal does not exist")
    })
    public ResponseEntity<Meal> getMeal(@PathVariable Long mealId) {
        return mealService.findMealById(mealId).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PutMapping("/{mealId}/update")
    @Operation(summary = "Update a Meal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meal updated", content = @Content(schema = @Schema(implementation = Meal.class)))
    })
    public ResponseEntity<Meal> updateMeal(@PathVariable @NonNull Long mealId,
                                           @RequestBody @NonNull MealInput mealUpdateInput) {
        var meal = mealService.updateValidatedMeal(mealId, mealUpdateInput);
        return ResponseEntity.status(HttpStatus.OK).body(meal);
    }

    @PatchMapping("/{mealId}/recipe/{recipeId}")
    @Operation(summary = "Set Recipe for Meal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe is set for Meal", content = @Content(schema = @Schema(implementation = Meal.class)))
    })
    public ResponseEntity<Meal> setRecipeForMeal(@PathVariable Long mealId, @PathVariable Long recipeId) {
        var meal = mealService.setRecipeForMeal(mealId, recipeId);
        return ResponseEntity.status(HttpStatus.OK).body(meal);
    }

    @PatchMapping("/{mealId}/food-time/{foodTime}")
    @Operation(summary = "Set new FoodTime for Meal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "new FoodTime is set for Meal", content = @Content(schema = @Schema(implementation = Meal.class)))
    })
    public ResponseEntity<Meal> setFoodTimeForMeal(@PathVariable Long mealId, @PathVariable String foodTime) {
        var meal = mealService.setFoodTimeForMeal(mealId, foodTime);
        return ResponseEntity.status(HttpStatus.OK).body(meal);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all existing Meals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Meals are received", content = @Content(schema = @Schema(implementation = Meal.class))),
            @ApiResponse(responseCode = "204", description = "There are no existing Meals")
    })
    public ResponseEntity<List<Meal>> getAllExistingMeals() {
        var listOfMeals = mealService.findAllMeals();

        if (listOfMeals.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } else {
            return ResponseEntity.status(HttpStatus.OK).body(listOfMeals);
        }
    }

    @GetMapping("/food-time/{foodTime}")
    @Operation(summary = "Get all Meals by FoodTime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Meals for this FoodTime are received", content = @Content(schema = @Schema(implementation = Meal.class))),
            @ApiResponse(responseCode = "204", description = "There are no Meals for that FoodTime")
    })
    public ResponseEntity<List<Meal>> getAllMealsByFoodTime(@PathVariable String foodTime) {
        var listOfMeals = mealService.getMealsByFoodTime(foodTime);

        if (listOfMeals.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } else {
            return ResponseEntity.status(HttpStatus.OK).body(listOfMeals);
        }
    }

    @GetMapping
    @Operation(summary = "Get Meals by FoodTime and Kcal within a selected range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Meals are received", content = @Content(schema = @Schema(implementation = Meal.class))),
            @ApiResponse(responseCode = "204", description = "There are no existing Meals")
    })
    public ResponseEntity<List<Meal>> getMealsByFoodTimeWithinRange(
            @RequestParam FoodTime foodTime,
            @RequestParam Double minKcal,
            @RequestParam Double maxKcal
    ) {
        var listOfMeals = mealService.getMealsByFoodTimeWithinKcalRange(foodTime, minKcal, maxKcal);

        if (listOfMeals.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } else {
            return ResponseEntity.status(HttpStatus.OK).body(listOfMeals);
        }
    }

    @DeleteMapping("/{mealId}")
    @Operation(summary = "Delete a Meal by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meal deleted")
    })
    public ResponseEntity<Void> deleteMeal(@PathVariable Long mealId) {
        mealService.deleteMeal(mealId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}