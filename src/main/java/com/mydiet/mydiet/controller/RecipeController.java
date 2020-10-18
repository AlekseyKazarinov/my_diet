package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.dto.RecipeCreationInput;
import com.mydiet.mydiet.domain.entity.Recipe;
import com.mydiet.mydiet.service.RecipeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @ApiOperation(value = "Create a new Recipe")
    @ApiResponses(value = @ApiResponse(code = 201, message = "Recipe created", response = Recipe.class))
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody @NonNull RecipeCreationInput recipeCreationInput) {
        var recipe = recipeService.createValidatedRecipe(recipeCreationInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipe);

    }

    @ApiOperation(value = "Get a Recipe")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Recipe recieved", response = Recipe.class))
    @GetMapping(path = "/{recipeId}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable @NonNull Long recipeId) {
        var optionalRecipe = recipeService.findRecipeById(recipeId);

        if (optionalRecipe.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(optionalRecipe.get());

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @ApiOperation(value = "Get All Recipes")
    @ApiResponses(value = @ApiResponse(code = 200, message = "All Recipes recieved", response = Recipe[].class))
    @GetMapping(path = "/all")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        var recipeList = recipeService.findAllRecipes();

        if (!recipeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(recipeList);

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @ApiOperation(value = "Get Resipes sorted by similarity in calories")
    @ApiResponses(value = @ApiResponse(code = 200, message = "All Sorted Recipes recieved", response = Recipe[].class))
    @GetMapping(path = "/sorted-by-calories")
    public ResponseEntity<List<Recipe>> getAllSortedRecipes(@RequestParam Integer kkal, @RequestParam Integer maxCount) {
        var recipeList = recipeService.findAllRecipesSortedBySimilarityInCalories(kkal, maxCount);

        if (!recipeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(recipeList);

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }



    /*
    Create a new recipe:
POST /recipes
	201 Created -> returns recipeId
Response body:
{
	"recipeId": long
}

Update a recipe:
PUT /recipes/{recipeId}
	200 OK
        404 Not Found {recipeId}
Response body = recipe

Update image for a recipe:
PATCH /recipes/{recipeId}/image
	200 OK
        404 Not Found {recipeId}
Response body:
{
	"imageId": long
}

Update description for a recipe:
PATCH /recipes/{recipeId}/description
	200 OK
        404 Not Found {recipeId}

Update list of products for a recipe:
PUT /recipes/{recipeId}/products
	200 OK
        404 Not Found {recipeId}

Add a product to recipe:
POST /recipes/{recipeId}/products
	200 OK
        404 Not Found {recipeId}

Update elements of a recipe:

PATCH /recipes/{recipeId}/kcal
PATCH /recipes/{recipeId}/day
PATCH /recipes/{recipeId}/food-time
PATCH /recipes/{recipeId}/ingredients
PATCH /recipes/{recipeId}/program-number
PATCH /recipes/{recipeId}/name

     */

}


