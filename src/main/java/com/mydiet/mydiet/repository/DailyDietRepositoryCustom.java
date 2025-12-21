package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.DailyDiet;
import com.mydiet.mydiet.domain.entity.Meal;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DailyDietRepositoryCustom {

    public Optional<DailyDiet> findByMeals(Set<Meal> meals);
}
