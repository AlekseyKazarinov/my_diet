package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.Lifestyle;
import com.mydiet.mydiet.domain.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findRecipeByName(String name);
    Optional<Recipe> findRecipeByLangIdAndLanguage(String langId, Language language);

    Page<Recipe> findAllByLanguageAndTotalKcalLessThanEqualOrderByTotalKcalDesc(Language language, Double totalKcal, Pageable pageRequest);
    Page<Recipe> findAllByLanguageAndTotalKcalGreaterThanOrderByTotalKcalAsc(Language language, Double totalKcal, Pageable pageRequest);

    @Query(value = "SELECT r FROM RECIPE r JOIN r.lifestyles s WHERE s = ?1", nativeQuery = true)
    List<Recipe> retrieveByTag(Lifestyle lifestyle);

    @Query(value = "SELECT r FROM RECIPE r JOIN r.lifestyles s WHERE r.NAME = ?1 AND s = ?2", nativeQuery = true)
    List<Recipe> retrieveByNameFilterByTag(String name, Lifestyle lifestyle);

}
