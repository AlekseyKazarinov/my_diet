package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findRecipeByName(String name);

}
