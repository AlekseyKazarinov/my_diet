package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.input.NutritionProgramInput;
import com.mydiet.mydiet.domain.dto.output.NutritionProgramOutput;
import com.mydiet.mydiet.domain.entity.DailyDiet;
import com.mydiet.mydiet.domain.entity.NutritionProgram;
import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.repository.NutritionProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NutritionProgramService {

    private final DailyDietService dailyDietService;
    private final NutritionProgramRepository nutritionProgramRepository;

    public NutritionProgram createValidatedNutritionProgram(NutritionProgramInput programCreationInput) {
        validateNutritionProgramInput(programCreationInput);
        return createNutritionProgram(programCreationInput);
    }

    private void validateNutritionProgramInput(NutritionProgramInput programInput) {
        Utils.validateStringFieldIsSet(programInput.getName(), "name", programInput);
        Utils.validateStringFieldIsSet(programInput.getDescription(), "description", programInput);
        //Utils.validateStringFieldIsSet(programInput.getBackgroundColour(), "background colour", programInput);
        var numberOfMeals = programInput.getDailyNumberOfMeals();
        Utils.validateFieldIsNonNegative(numberOfMeals, "number of meals", programInput);

        for (var dailyDietInput : programInput.getDailyDietInputs()) {
            dailyDietService.validateDailyDietInput(dailyDietInput);
            dailyDietService.validateDailyDietInputContainsNumberOfMealsEqualTo(numberOfMeals, dailyDietInput);
        }
    }

    private NutritionProgram createNutritionProgram(NutritionProgramInput input) {
        var dailyDietList = new ArrayList<DailyDiet>();

        for (var dailyDietInput : input.getDailyDietInputs()) {
            dailyDietList.add(dailyDietService.createDailyDiet(dailyDietInput));
        }

        var nutritionProgram = NutritionProgram.builder()
                .name(input.getName())
                .description(input.getDescription())
                .backgroundColour(input.getBackgroundColour())
                .dailyNumberOfMeals(input.getDailyNumberOfMeals())
                .dailyDietList(dailyDietList)
                .build();

        return saveIfOriginal(nutritionProgram);
    }

    private NutritionProgram saveIfOriginal(NutritionProgram nutritionProgram) {
        var example = Example.of(nutritionProgram);
        var optionalStoredProgram = nutritionProgramRepository.findOne(example);

        if (optionalStoredProgram.isPresent()) {
            log.info("Nutrition Program with name: {} already exists", nutritionProgram.getName());
            return optionalStoredProgram.get();
        }

        return nutritionProgramRepository.save(nutritionProgram);
    }

    public Optional<NutritionProgram> findNutritionProgram(Long programNumber) {
        return nutritionProgramRepository.findById(programNumber);
    }

    public NutritionProgram getProgramOrElseThrow(Long programNumber) {
        return nutritionProgramRepository.findById(programNumber)
                .orElseThrow(
                    () -> new NotFoundException(String.format("Nutrition Program #%s does not exist", programNumber))
                );
    }

    public Long getTotalNumberOfPrograms() {
        return nutritionProgramRepository.count();
    }

    public Optional<NutritionProgramOutput> getBriefFormOfNutritionProgram(Long programNumber) {
        var optionalProgram = findNutritionProgram(programNumber);

        if (optionalProgram.isEmpty()) {
            return Optional.empty();
        }

        var program = optionalProgram.get();

        // todo: implement a brief form for output

        return Optional.empty();
    }

    public Integer getNumberOfWeeksFor(NutritionProgram nutritionProgram) {
        var numberOfDays = nutritionProgram.getDailyDietList().size();
        var fullWeeks = numberOfDays / 7;
        var remainder = numberOfDays % 7;

        if (remainder == 0) {
            return fullWeeks;
        } else {
            return ++fullWeeks;
        }
    }

    public List<DailyDiet> getDailyDietsForWeekNo(Integer weekNumber, NutritionProgram nutritionProgram) {
        Assert.notNull(weekNumber, "weekNumber must not be null");

        var weeks = getNumberOfWeeksFor(nutritionProgram);

        if (weekNumber > weeks) {
            return Collections.emptyList();
        }

        var dailyDiets = nutritionProgram.getDailyDietList();
        var firstDay = 7 * (weekNumber - 1);
        var lastDay = Math.min( 7 * weekNumber, dailyDiets.size());

        return dailyDiets.subList(firstDay, lastDay);
    }

    public Set<Product> getListOfProductsFor(NutritionProgram nutritionProgram) {
        var productSet = new HashSet<Product>();

        nutritionProgram.getDailyDietList()
                .forEach(diet -> {
                    diet.getMeals()
                        .forEach(meal -> {
                                    meal.getRecipe().getIngredients()
                                    .forEach(ingredient -> productSet.add(ingredient.getProduct()));
                                });
                });

        return productSet;
    }

    // todo: list of products taking into account amount

}
