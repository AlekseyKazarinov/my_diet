package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.DailyDietInput;
import com.mydiet.mydiet.domain.entity.DailyDiet;
import com.mydiet.mydiet.domain.entity.Meal;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.repository.DailyDietRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;


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
        var listOfMeals = new HashSet<Meal>();

        for (var mealId : dailyDietCreationInput.getMealIds()) {
            listOfMeals.add(mealService.getMealOrElseThrow(mealId));
        }

        var dailyDiet = DailyDiet.builder()
                .name(dailyDietCreationInput.getName())
                .meals(listOfMeals)
                .build();

        return saveIfOriginal(dailyDiet);
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
    }

    public void validateDailyDietInputContainsNumberOfMealsEqualTo(
            Short expectedNumberOfMeals, DailyDietInput dailyDietInput
    ) {
       var dailyDietNumberOfMeals = dailyDietInput.getMealIds().size();
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

    public DailyDiet updateDailyDietName(Long dailyDietId, String newDailyDietName) {
        Utils.validateTextFieldIsSet(newDailyDietName, "Daily Diet name");
        var dailyDiet = getDailyDietOrElseThrow(dailyDietId);
        dailyDiet.setName(newDailyDietName);
        return dailyDietRepository.save(dailyDiet);
    }

    public void deleteDailyDiet(Long dailyDietId) {
        dailyDietRepository.deleteById(dailyDietId);
    }

}
