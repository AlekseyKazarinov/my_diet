package com.mydiet.mydiet.repository;

import com.google.common.collect.Lists;
import com.mydiet.mydiet.domain.entity.DailyDiet;
import com.mydiet.mydiet.domain.entity.Meal;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class DailyDietRepositoryImpl implements DailyDietRepositoryCustom {

    private final DailyDietRepository dailyDietRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<DailyDiet> findByMeals(Set<Meal> meals) {
        var query = entityManager.createNativeQuery(
                "SELECT   dm.DAILY_DIET_ID FROM DAILY_DIET_MEAL dm\n" +
                "GROUP BY DAILY_DIET_ID\n" +
                "HAVING   SUM(dm.MEAL_ID IN :meals) = COUNT(*)\n" +
                "     AND COUNT(DISTINCT dm.MEAL_ID) = :cnt");

        query.setParameter("meals", Lists.transform(Lists.newArrayList(meals), Meal::getId));
        query.setParameter("cnt", meals.size());
        var resultList = query.getResultList();
        var dailyDietId = resultList == null || resultList.isEmpty() ? null : resultList.get(0);

        if (dailyDietId == null) {
            return Optional.empty();
        }

        var id =((Number) dailyDietId).longValue();

        return dailyDietRepository.findById(id);
    }
}
