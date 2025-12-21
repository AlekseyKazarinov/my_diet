package com.mydiet.mydiet.service;

import com.google.common.collect.Sets;
import com.mydiet.mydiet.domain.dto.input.DailyDietInput;
import com.mydiet.mydiet.domain.entity.DailyDiet;
import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.Lifestyle;
import com.mydiet.mydiet.domain.entity.Meal;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.repository.DailyDietRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mydiet.mydiet.domain.entity.Language.areEqual;


@Slf4j
@Service
@RequiredArgsConstructor
public class DailyDietService {

    private final DailyDietRepository dailyDietRepository;
    private final MealService         mealService;

    public DailyDiet createValidatedDailyDiet(DailyDietInput dailyDietCreationInput) {
        validateDailyDietInput(dailyDietCreationInput);
        return createDailyDiet(dailyDietCreationInput);
    }

    public DailyDiet createDailyDiet(DailyDietInput dailyDietCreationInput) {
        var lifestyles = dailyDietCreationInput.getLifestyles();

        var listOfMeals = dailyDietCreationInput.getMealIds().stream()
                .map(mealService::getMealOrElseThrow)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(lifestyles)) {
            lifestyles = deriveLifestylesFromMeals(listOfMeals);
        }

        var dailyDiet = DailyDiet.builder()
                .name(dailyDietCreationInput.getName())
                .meals(listOfMeals)
                .lifestyles(lifestyles)
                .build();

        return saveIfOriginal(dailyDiet);
    }

    private Set<Lifestyle> deriveLifestylesFromMeals(Set<Meal> meals) {
        return meals.stream()
                .map(meal -> meal.getRecipe().getLifestyles())
                .reduce(Sets::intersection)
                .orElse(Collections.emptySet());
    }

    public DailyDiet saveIfOriginal(DailyDiet dailyDiet) {
        var meals = dailyDiet.getMeals();
        var optionalStoredDailyDiet = dailyDietRepository.findByMeals(meals);

        if (optionalStoredDailyDiet.isPresent()) {
            log.info("Daily Diet with Meal Ids: {} already exists", dailyDiet.getMeals()
                    .stream()
                    .mapToLong(Meal::getId)
                    .toArray());
            return optionalStoredDailyDiet.get();
        }
        return dailyDietRepository.save(dailyDiet);
    }

    public Optional<DailyDiet> findDailyDietById(Long dailyDietId) {
        return dailyDietRepository.findById(dailyDietId);
    }

    public DailyDiet getDailyDietOrElseThrow(Long dailyDietId) {
        return findDailyDietById(dailyDietId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Daily Diet with id: %s does not exist", dailyDietId))
                );
    }

    public void validateDailyDietInput(DailyDietInput dailyDietInput) {
        var mealIds = dailyDietInput.getMealIds();
        Utils.validateCollectionContainsElements(mealIds, "mealIds", dailyDietInput);

        if (!CollectionUtils.isEmpty(dailyDietInput.getLifestyles())) {
            dailyDietInput.getMealIds().stream()
                    .map(mealService::getMealOrElseThrow)
                    .forEach(meal -> {
                        if (!meal.getRecipe().getLifestyles().containsAll(dailyDietInput.getLifestyles())) {

                            throw new ValidationException(
                                    String.format("Daily Diet lifestyles: %s are not in lifestyles for Meal #%s: %s",
                                            dailyDietInput.getLifestyles(),
                                            meal.getId(),
                                            meal.getRecipe().getLifestyles()));
                        }
                    });
        }

        validateLanguages(dailyDietInput);
    }

    private void validateLanguages(DailyDietInput dailyDietInput) {
        Language language = null;
        var captured = false;

        for (var mealId: dailyDietInput.getMealIds()) {
            var meal = mealService.getMealOrElseThrow(mealId);

            if (!captured) {
                language = Optional.ofNullable(meal.getRecipe().getLanguage()).orElse(Language.RUSSIAN);
                captured = true;
                continue;
            }

            if (!areEqual(language, meal.getRecipe().getLanguage())) {
                throw new ValidationException(String.format("Meals with ids %s have no consistent language", dailyDietInput.getMealIds()));
            }
        }
    }

    public void validateDailyDietInputContainsNumberOfMealsEqualTo(Short expectedNumberOfMeals, Long dailyDietId) {
       var dailyDietNumberOfMeals = getDailyDietOrElseThrow(dailyDietId).getMeals().size();

       if (dailyDietNumberOfMeals != expectedNumberOfMeals) {
           var message = String.format(
                   "Inconsistent Daily Diet input: number of Meals is %s but expected: %s",
                   dailyDietNumberOfMeals,
                   expectedNumberOfMeals
                   );
           log.error(message);

           throw new ValidationException(message);
       }
    }

    public DailyDiet updateDailyDiet(Long dailyDietId, DailyDietInput dailyDietUpdateInput) {
        validateDailyDietInput(dailyDietUpdateInput);
        var dailyDiet = getDailyDietOrElseThrow(dailyDietId);

        var lifestyles = dailyDietUpdateInput.getLifestyles();

        var listOfMeals = dailyDietUpdateInput.getMealIds().stream()
                .map(mealService::getMealOrElseThrow)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(lifestyles)) {
            lifestyles = deriveLifestylesFromMeals(listOfMeals);
        }

        dailyDiet.setLifestyles(lifestyles);
        dailyDiet.setMeals(listOfMeals);
        dailyDiet.setName(dailyDietUpdateInput.getName());

        return saveIfOriginal(dailyDiet);
    }

    public DailyDiet updateDailyDietName(Long dailyDietId, String newDailyDietName) {
        Utils.validateTextVariableIsSet(newDailyDietName, "Daily Diet name");
        var dailyDiet = getDailyDietOrElseThrow(dailyDietId);
        dailyDiet.setName(newDailyDietName);
        return dailyDietRepository.save(dailyDiet);
    }

    public void deleteDailyDiet(Long dailyDietId) {
        dailyDietRepository.deleteById(dailyDietId);
    }

}
