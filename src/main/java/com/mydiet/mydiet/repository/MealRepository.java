package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.FoodTime;
import com.mydiet.mydiet.domain.entity.Meal;
import com.mydiet.mydiet.domain.entity.Recipe;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MealRepository extends CrudRepository<Meal, Long> {

    public List<Meal> findMealsByFoodTime(FoodTime foodTime);
    public Optional<Meal> findByRecipeAndFoodTime(Recipe recipe, FoodTime foodTime);

}
