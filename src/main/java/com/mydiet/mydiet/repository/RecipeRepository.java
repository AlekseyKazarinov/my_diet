package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.domain.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findRecipeByName(String name);
    Page<Recipe> findAllByTotalKkalLessThanEqualOrderByTotalKkalDesc(Double totalKkal, Pageable pageRequest);
    Page<Recipe> findAllByTotalKkalGreaterThanOrderByTotalKkalAsc(Double totalKkal, Pageable pageRequest);
}
