package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.FoodTime;
import com.mydiet.mydiet.domain.entity.Meal;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MealRepository extends CrudRepository<Meal, Long> {
    public List<Meal> findMealsByFoodTime(FoodTime foodTime);
}
