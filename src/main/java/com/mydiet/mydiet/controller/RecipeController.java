package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.domain.dto.input.RecipeInput;
import com.mydiet.mydiet.domain.dto.input.RecipeTranslationInput;
import com.mydiet.mydiet.domain.entity.Image;
import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.Recipe;
import com.mydiet.mydiet.service.RecipeService;
import io.swagger.annotations.Api;
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
@Api(tags = "Recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @ApiOperation(value = "Create a new Recipe")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Recipe created", response = Recipe.class),
            @ApiResponse(code = 400, message = "Validation error", response = ErrorMessage.class)
    })
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody @NonNull RecipeInput recipeCreationInput) {
        var recipe = recipeService.createValidatedRecipe(recipeCreationInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipe);
    }

    @ApiOperation(value = "Translate an existing Recipe")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Recipe translated", response = Recipe.class),
            @ApiResponse(code = 400, message = "Validation error", response = ErrorMessage.class)
    })
    @PostMapping("{recipeId}/translate")
    public ResponseEntity<Recipe> translateRecipe(
            @PathVariable Long recipeId,
            @RequestBody @NonNull RecipeTranslationInput recipeTranslationInput
    ) {
        var recipe = recipeService.translateValidatedRecipe(recipeId, recipeTranslationInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipe);
    }

    @ApiOperation(value = "Get a Recipe")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Recipe received", response = Recipe.class),
                           @ApiResponse(code = 204, message = "There is no Recipe with that id", response = Object.class)})
    @GetMapping(path = "/{recipeId}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable @NonNull Long recipeId) {
        var optionalRecipe = recipeService.findRecipeById(recipeId);

        if (optionalRecipe.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(optionalRecipe.get());

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    /**
     * This is very resource intensive endpoint
     * @return all recipes stored in the database
     */
    @ApiOperation(value = "Get All Recipes (Use this endpoint judiciously, this endpoint is highly resource-consuming)")
    @ApiResponses(value = @ApiResponse(code = 200, message = "All Recipes received", response = Recipe[].class))
    @GetMapping(path = "/all")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        var recipeList = recipeService.findAllRecipes();

        if (!recipeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(recipeList);

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @ApiOperation(value = "Get Recipes sorted by similarity in calories")
    @ApiResponses(value = @ApiResponse(code = 200, message = "All Sorted Recipes received", response = Recipe[].class))
    @GetMapping(path = "/sorted-by-calories")
    public ResponseEntity<List<Recipe>> getAllSortedRecipes(
            @RequestParam(defaultValue = "RUSSIAN") Language language,
            @RequestParam Integer kcal,
            @RequestParam Integer maxNumber
    ) {
        var recipeList = recipeService.findAllRecipesSortedBySimilarityInCalories(language, kcal, maxNumber);

        if (!recipeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(recipeList);

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @PutMapping("/{recipeId}/update")
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable @NonNull Long recipeId,
            @RequestBody @NonNull RecipeInput recipeUpdateInput) {
        var recipe = recipeService.updateValidatedRecipe(recipeId, recipeUpdateInput);
        return ResponseEntity.status(HttpStatus.OK).body(recipe);
    }

    @ApiOperation(value = "Put an Image into a Recipe")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Image created for Recipe", response = Image.class))
    @PutMapping("/{recipeId}/image")
    public ResponseEntity<Image> addImageToRecipe(@PathVariable @NonNull Long recipeId,
                                                  @RequestParam @NonNull String imageName,
                                                  @RequestBody @NonNull String imageSource) {
        var image = recipeService.setImageForRecipe(recipeId, imageName, imageSource);
        return ResponseEntity.status(HttpStatus.OK).body(image);
    }

    // todo: delete Recipe

/*
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


