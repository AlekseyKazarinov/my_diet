package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.domain.dto.input.MealInput;
import com.mydiet.mydiet.domain.entity.Meal;
import com.mydiet.mydiet.service.MealService;
import io.swagger.annotations.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
@Api(tags = "Meals")
public class MealController {


    private final MealService mealService;

    @PostMapping
    @ApiOperation(value = "Create a new Meal")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Meal created", response = Meal.class),
            @ApiResponse(code = 400, message = "Validation error", response = ErrorMessage.class)})
    public ResponseEntity<Meal> createMeal(@RequestBody @NonNull MealInput mealCreationInput) {
        var meal = mealService.createValidatedMeal(mealCreationInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(meal);
    }

    @PutMapping("/{mealId}/update")
    @ApiOperation(value = "Update a Meal")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Meal updated", response = Meal.class))
    public ResponseEntity<Meal> updateMeal(@PathVariable @NonNull Long mealId,
                                           @RequestBody @NonNull MealInput mealUpdateInput) {
        var meal = mealService.updateValidatedMeal(mealId, mealUpdateInput);
        return ResponseEntity.status(HttpStatus.OK).body(meal);
    }

    @PatchMapping("/{mealId}/recipe/{recipeId}")
    @ApiOperation(value = "Set Recipe for Meal")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Recipe is set for Meal", response = Meal.class))
    public ResponseEntity<Meal> setRecipeForMeal(@PathVariable Long mealId, @PathVariable Long recipeId) {
        var meal = mealService.setRecipeForMeal(mealId, recipeId);
        return ResponseEntity.status(HttpStatus.OK).body(meal);
    }

    @PatchMapping("/{mealId}/food-time/{foodTime}")
    @ApiOperation(value = "Set new FoodTime for Meal")
    @ApiResponses(value = @ApiResponse(code = 200, message = "new FoodTime is set for Meal", response = Meal.class))
    public ResponseEntity<Meal> setFoodTimeForMeal(@PathVariable Long mealId, @PathVariable String foodTime) {
        var meal = mealService.setFoodTimeForMeal(mealId, foodTime);
        return ResponseEntity.status(HttpStatus.OK).body(meal);
    }

    @GetMapping
    @ApiOperation(value = "Get all existing Meals")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All Meals are received", response = Meal[].class),
            @ApiResponse(code = 204, message = "There are no existing Meals", response = Object.class)
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
    @ApiOperation(value = "Get all Meals by FoodTime")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All Meals for this FoodTime are received",response = Meal[].class),
            @ApiResponse(code = 204, message = "There are no Meals for that FoodTime", response = Object.class)
    })
    public ResponseEntity<List<Meal>> getAllMealsByFoodTime(@PathVariable String foodTime) {
        var listOfMeals = mealService.getMealsByFoodTime(foodTime);

        if (listOfMeals.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } else {
            return ResponseEntity.status(HttpStatus.OK).body(listOfMeals);
        }
    }


}
