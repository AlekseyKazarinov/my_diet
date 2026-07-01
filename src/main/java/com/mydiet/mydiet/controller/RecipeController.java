package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.domain.dto.input.RecipeInput;
import com.mydiet.mydiet.domain.dto.input.RecipeTranslationInput;
import com.mydiet.mydiet.domain.entity.Image;
import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.Recipe;
import com.mydiet.mydiet.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/recipes")
@RequiredArgsConstructor
@Tag(name = "Recipes") // Заменено с @Api(tags = ...)
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "Create a new Recipe") // Заменено с @ApiOperation
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipe created", content = @Content(schema = @Schema(implementation = Recipe.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody @NonNull RecipeInput recipeCreationInput) {
        var recipe = recipeService.createValidatedRecipe(recipeCreationInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipe);
    }

    @Operation(summary = "Translate an existing Recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipe translated", content = @Content(schema = @Schema(implementation = Recipe.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping("{recipeId}/translate")
    public ResponseEntity<Recipe> translateRecipe(
            @PathVariable Long recipeId,
            @RequestBody @NonNull RecipeTranslationInput recipeTranslationInput
    ) {
        var recipe = recipeService.translateValidatedRecipe(recipeId, recipeTranslationInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipe);
    }

    @Operation(summary = "Get a Recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe received", content = @Content(schema = @Schema(implementation = Recipe.class))),
            @ApiResponse(responseCode = "204", description = "There is no Recipe with that id") // Убран response = Object.class, так как для 204 тела ответа нет
    })
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
    @Operation(summary = "Get All Recipes (Use this endpoint judiciously, this endpoint is highly resource-consuming)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Recipes received", content = @Content(schema = @Schema(implementation = Recipe.class))) // Recipe[].class заменено на Recipe.class
    })
    @GetMapping(path = "/all")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        var recipeList = recipeService.findAllRecipes();

        if (!recipeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(recipeList);

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @Operation(summary = "Get Recipes sorted by similarity in calories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Sorted Recipes received", content = @Content(schema = @Schema(implementation = Recipe.class)))
    })
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

    @Operation(summary = "Put an Image into a Recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image created for Recipe", content = @Content(schema = @Schema(implementation = Image.class)))
    })
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