package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.dto.RecipeCreationInput;
import com.mydiet.mydiet.domain.entity.Recipe;
import com.mydiet.mydiet.domain.exception.NoContentException;
import com.mydiet.mydiet.repository.RecipeRepository;
import io.swagger.annotations.SwaggerDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeRepository recipeRepository;

    @PostMapping(path = "/")
    public Long createRecipe(@RequestBody RecipeCreationInput recipeCreationInput) {
        var recipe = Recipe.builder()
                .name(recipeCreationInput.getName())
                .description(recipeCreationInput.getDescription())
                .programNumber(recipeCreationInput.getProgramNumber())
                .day(recipeCreationInput.getDay())
                .foodTime(recipeCreationInput.getFoodTime())
                .build();

        return recipeRepository.save(recipe).getId();
    }

    @GetMapping(path = "/{recipeId}")
    public Recipe getRecipe(@PathVariable Long recipeId) {
        var optionalRecipe = recipeRepository.findById(recipeId);

        if (optionalRecipe.isPresent()) {
            return optionalRecipe.get();

        } else {
            var message = String.format("Recipe %s does not exist", recipeId);
            throw new NoContentException(message);
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


