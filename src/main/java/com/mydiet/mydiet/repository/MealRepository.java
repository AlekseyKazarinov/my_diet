package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.FoodTime;
import com.mydiet.mydiet.domain.entity.Meal;
import com.mydiet.mydiet.domain.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {

    public List<Meal> findMealsByFoodTime(FoodTime foodTime);

    public List<Meal> findMealByFoodTimeAndRecipe_TotalKcalBetween(FoodTime foodTime, Double totalKcalLeft, Double totalKcalRight);

}
