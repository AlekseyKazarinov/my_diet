package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.Lifestyle;
import com.mydiet.mydiet.domain.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findRecipeByName(String name);
    Page<Recipe> findAllByLanguageAndTotalKcalLessThanEqualOrderByTotalKcalDesc(Language language, Double totalKcal, Pageable pageRequest);
    Page<Recipe> findAllByLanguageAndTotalKcalGreaterThanOrderByTotalKcalAsc(Language language, Double totalKcal, Pageable pageRequest);

    @Query("SELECT r FROM RECIPE r JOIN r.lifestyles s WHERE s = :lifestyle")
    List<Recipe> retrieveByTag(@Param("lifestyle") Lifestyle lifestyle);

    @Query("SELECT r FROM RECIPE r JOIN r.lifestyles s WHERE r.NAME = :name AND s = :lifestyle")
    List<Recipe> retrieveByNameFilterByTag(@Param("name") String name, @Param("lifestyle") Lifestyle lifestyle);

}
