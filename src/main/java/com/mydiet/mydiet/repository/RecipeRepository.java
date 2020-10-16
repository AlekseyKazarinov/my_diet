package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {

    Optional<Recipe> findRecipeByName(String name);

}
