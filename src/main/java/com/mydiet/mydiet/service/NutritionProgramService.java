package com.mydiet.mydiet.service;

import com.google.common.collect.Sets;
import com.mydiet.mydiet.domain.dto.input.NutritionProgramInput;
import com.mydiet.mydiet.domain.dto.input.ProductExclusion;
import com.mydiet.mydiet.domain.dto.output.NutritionProgramOutput;
import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.infrastructure.ShoppingListService;
import com.mydiet.mydiet.repository.NutritionProgramRepository;
import com.mydiet.mydiet.repository.ShoppingListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.mydiet.mydiet.domain.entity.Status.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NutritionProgramService {

    private final DailyDietService dailyDietService;
    private final NutritionProgramRepository nutritionProgramRepository;
    private final ShoppingListService shoppingListService;
    private final ShoppingListRepository shoppingListRepository;

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
        Utils.validateCollectionContainsElements(programInput.getDailyDietIds(), "Daily Diet Ids", programInput);

        for (var dailyDietId : programInput.getDailyDietIds()) {
            dailyDietService.validateDailyDietInputContainsNumberOfMealsEqualTo(numberOfMeals, dailyDietId);
        }

        validateLifestyles(programInput);
    }

    private void validateLifestyles(NutritionProgramInput programInput) {
        if (CollectionUtils.isEmpty(programInput.getLifestyles())) {
            return;
        }

        for (var dailyDietId : programInput.getDailyDietIds()) {
            var dailyDiet = dailyDietService.getDailyDietOrElseThrow(dailyDietId);

            if (!dailyDiet.getLifestyles().containsAll(programInput.getLifestyles())) {
                throw new ValidationException(
                        String.format("Daily Diet %s does not contain Nutrition Program lifestyles. Expected: %s to be in %s",
                                dailyDietId,
                                programInput.getLifestyles(),
                                dailyDiet.getLifestyles()
                        )
                );
            }
        }
    }

    private NutritionProgram createNutritionProgram(NutritionProgramInput input) {
        var dailyDietList = new ArrayList<DailyDiet>();

        for (var dailyDietId : input.getDailyDietIds()) {
            dailyDietList.add(dailyDietService.getDailyDietOrElseThrow(dailyDietId));
        }

        var lifestyles = input.getLifestyles();

        if (CollectionUtils.isEmpty(lifestyles)) {
            lifestyles = dailyDietList.stream()
                    .map(dailyDiet -> {
                        return dailyDiet.getLifestyles();
                    })
                    .reduce(Sets::intersection)
                    .orElse(Collections.emptySet());
        }

        var nutritionProgram = NutritionProgram.builder()
                .name(input.getName())
                .description(input.getDescription())
                .additionalInfo(input.getAdditionalInfo())
                .lifestyles(lifestyles)
                .backgroundColour(input.getBackgroundColour())
                .dailyNumberOfMeals(input.getDailyNumberOfMeals())
                .dailyDiets(dailyDietList)
                .status(DRAFT)
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

    public NutritionProgram setStatusFor(NutritionProgram program, Status status) {
        if (program.getStatus() == status) {
            return program;
        }

        switch (status) {
            case DRAFT:
                shoppingListRepository.deleteById(program.getNumber());
                break;
            case ACCEPTED:
                shoppingListService.generateShoppingListFor(program);
                break;
            case PUBLISHED:
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Unable to set status %s for Nutrition Program %s", status, program.getNumber())
                );
        }
        program.setStatus(status);
        return nutritionProgramRepository.save(program);
    }

    public NutritionProgram revertStatusFor(NutritionProgram program) {
        if (program.getStatus() == null) {
            log.warn("Can not revert Nutrition Program {} with null status. Ignore", program.getNumber());
            return program;
        }

        switch (program.getStatus()) {
            case DRAFT:
                return program;
            case ACCEPTED:
                program.setStatus(DRAFT);
                shoppingListRepository.deleteById(program.getNumber());
                return nutritionProgramRepository.save(program);
            case PUBLISHED:
                program.setStatus(ACCEPTED);
                return nutritionProgramRepository.save(program);
            default:
                throw new IllegalArgumentException(
                        String.format("Unable to revise status %s for Nutrition Program %s", program.getStatus(), program.getNumber())
                );
        }
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
        var numberOfDays = nutritionProgram.getDailyDiets().size();
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

        var dailyDiets = nutritionProgram.getDailyDiets();
        var firstDay = 7 * (weekNumber - 1);
        var lastDay = Math.min( 7 * weekNumber, dailyDiets.size());

        return dailyDiets.subList(firstDay, lastDay);
    }

    public Set<Product> getListOfProductsFor(NutritionProgram nutritionProgram) {
        var productSet = new HashSet<Product>();

        nutritionProgram.getDailyDiets()
                .forEach(diet -> {
                    diet.getMeals()
                        .forEach(meal -> {
                                    meal.getRecipe().getIngredients()
                                    .forEach(ingredient -> productSet.add(ingredient.getProduct()));
                                });
                });

        return productSet;
    }

    public NutritionProgram acceptProgram(Long programNumber) {
        var program = getProgramOrElseThrow(programNumber);
        return setStatusFor(program, ACCEPTED);
    }

    public NutritionProgram publishProgram(Long programNumber) {
        var program = getProgramOrElseThrow(programNumber);
        return setStatusFor(program, PUBLISHED);
    }

    public NutritionProgram revertProgram(Long programNumber) {
        var program = getProgramOrElseThrow(programNumber);
        return revertStatusFor(program);
    }

    // todo: implement
    public List<NutritionProgram> getProgramsByKcal(Integer kcal, Integer maxNumber) {

        return Collections.emptyList();
    }

    // todo: whether we need these methods. We should analyze potential user cases.

    public List<NutritionProgram> getPublishedPrograms(Integer kcal, Set<Lifestyle> lifestyles, ProductExclusion productExclusion) {
        // todo: implement
        return Collections.emptyList();
    }

    public List<NutritionProgram> getProgramsBy(Integer kcal, Status status, Set<Lifestyle> lifestyles, ProductExclusion productExclusion) {
        // todo: implement
        return Collections.emptyList();
    }
}
