package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.entity.Recipe;
import com.mydiet.mydiet.domain.exception.GenericException;
import com.mydiet.mydiet.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeStorageService {

    private final RecipeRepository recipeRepository;

    public Recipe saveIfOriginal(Recipe recipe) {
        try {
            return recipeRepository.findOne(Example.of(recipe))
                    .orElseGet(() -> recipeRepository.save(recipe));

        } catch (Exception e) {
            log.error("An error occurred when finding example of Recipe {}", recipe);
            throw new GenericException("Failed when trying to find the same Recipe", e);
        }
        /*return recipeRepository.findRecipeByName(recipe.getName())
                               .orElseGet(() -> recipeRepository.save(recipe));*/
    }
}
