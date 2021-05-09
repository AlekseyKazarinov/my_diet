package com.mydiet.mydiet.service;

import com.google.common.collect.Sets;
import com.mydiet.mydiet.domain.dto.input.BaseNutritionProgramInput;
import com.mydiet.mydiet.domain.dto.input.NutritionProgramInput;
import com.mydiet.mydiet.domain.dto.input.ProductExclusion;
import com.mydiet.mydiet.domain.dto.input.ProgramTranslationInput;
import com.mydiet.mydiet.domain.dto.output.NutritionProgramOutput;
import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.domain.exception.BadRequestException;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.infrastructure.ShoppingListService;
import com.mydiet.mydiet.repository.NutritionProgramRepository;
import com.mydiet.mydiet.repository.ShoppingListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mydiet.mydiet.domain.entity.Language.areEqual;
import static com.mydiet.mydiet.domain.entity.Status.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NutritionProgramService {

    private static final Integer DEFAULT_MAX_NUMBER = 5;
    private static final String DEFAULT_COLOR = "#FFFFFF";
    private static final Language DEFAULT_LANGUAGE = Language.RUSSIAN;

    private final DailyDietService dailyDietService;
    private final NutritionProgramRepository nutritionProgramRepository;
    private final ShoppingListService shoppingListService;
    private final ShoppingListRepository shoppingListRepository;
    private final NutritionProgramStorageService programStorageService;
    private final RecipeService recipeService;

    public NutritionProgram createValidatedNutritionProgram(NutritionProgramInput programCreationInput) {
        validateNutritionProgramInput(programCreationInput);
        return createNutritionProgram(programCreationInput);
    }

    private void validateNutritionProgramInput(NutritionProgramInput programInput) {
        validateBaseNutritionProgramInput(programInput);

        var numberOfMeals = programInput.getDailyNumberOfMeals();
        Utils.validateFieldIsNonNegative(numberOfMeals, "number of meals", programInput);
        Utils.validateCollectionContainsElements(programInput.getDailyDietIds(), "Daily Diet Ids", programInput);

        for (var dailyDietId : programInput.getDailyDietIds()) {
            dailyDietService.validateDailyDietInputContainsNumberOfMealsEqualTo(numberOfMeals, dailyDietId);
        }

        validateLifestyles(programInput);
        validateLanguage(programInput);
    }

    private void validateBaseNutritionProgramInput(BaseNutritionProgramInput programInput) {
        Utils.validateTextFieldIsSet(programInput.getName(), "name", programInput);
        Utils.validateTextFieldIsSet(programInput.getDescription(), "description", programInput);
        Utils.validateTextFieldIsSet(programInput.getShortDescription(), "shortDescription", programInput);
        //Utils.validateTextFieldIsSet(programInput.getBackgroundColour(), "background colour", programInput);
    }

    public NutritionProgram translateValidatedNutritionProgram(
            Long programNumber,
            ProgramTranslationInput programTranslationInput
    ) {
        Utils.validateTextFieldIsSet(programTranslationInput.getName(), "name", programTranslationInput);
        Utils.validateTextFieldIsSet(programTranslationInput.getDescription(), "description", programTranslationInput);
        Utils.validateTextFieldIsSet(programTranslationInput.getShortDescription(), "shortDescription", programTranslationInput);

        var program = programStorageService.getProgramOrElseThrow(programNumber);

        if (Language.areEqual(program.getLanguage(), programTranslationInput.getLanguage())) {
            throw new ValidationException("Nutrition Program can not be translated into the same language");
        }

        var optionalAlreadyTranslatedRecipe = nutritionProgramRepository.findProgramByLangIdAndLanguage(
                program.getLangId(), program.getLanguage()
        );

        if (optionalAlreadyTranslatedRecipe.isPresent()) {
            throw new ValidationException(
                    String.format("Translation into %s for Nutrition Program with Number #%s is Nutrition Program #%s",
                            Language.print(programTranslationInput.getLanguage()),
                            program.getNumber(),
                            optionalAlreadyTranslatedRecipe.get().getNumber()
                    ));
        }

        if (!StringUtils.isEmpty(program.getAdditionalInfo()) && StringUtils.isEmpty(programTranslationInput.getAdditionalInfo())) {
            log.warn("ProgramTranslationInput does not contain additionalInfo unlike original Nutrition Program #{} in {} language",
                    program.getNumber(), Language.print(programTranslationInput.getLanguage())
            );
        }


        // todo: implement validation and copying all nested entities (recipes, meals, daily diets)
        /*program.getDailyDiets().stream()
                .flatMap(dailyDiet -> dailyDiet.getMeals().stream())
                .map(Meal::getRecipe)
                .map(recipe -> recipeService.findRecipeTranslationInto(programTranslationInput.getLanguage(), recipe)
                        .orElseThrow(() -> new ValidationException(
                                String.format("Recipe #%s has no translation into %s language for ProgramTranslationInput",
                                        recipe.getId(),
                                        programTranslationInput.getLanguage())
                                ))
                )*/


        program.setNumber(null);
        program.setLanguage(programTranslationInput.getLanguage());

        program.setName(programTranslationInput.getName());
        program.setDescription(programTranslationInput.getDescription());
        program.setShortDescription(programTranslationInput.getShortDescription());
        program.setAdditionalInfo(programTranslationInput.getAdditionalInfo());

        return nutritionProgramRepository.save(program);
    }

    private void validateLanguage(NutritionProgramInput programInput) {
        var programLanguage = programInput.getLanguage();
        var message = "Non consistent language settings! Program language: %s but language for Recipe %s (id = %s) is %s";

        programInput.getDailyDietIds().stream()
                .map(dailyDietService::getDailyDietOrElseThrow)
                .flatMap(dailyDiet -> dailyDiet.getMeals().stream())
                .forEach(meal -> {
                    var recipe = meal.getRecipe();

                    if (!areEqual(programLanguage, recipe.getLanguage())) {
                        throw new ValidationException(String.format(
                                message, programLanguage, recipe.getName(), recipe.getId(), recipe.getLanguage()
                        ));
                    }
                });
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
                .langId(UUID.randomUUID().toString())
                .language(Optional.ofNullable(input.getLanguage()).orElse(Language.RUSSIAN))
                .lifestyles(lifestyles)
                .dayColor(Optional.ofNullable(input.getDayColor()).orElse(DEFAULT_COLOR))
                .mainColor(Optional.ofNullable(input.getMainColor()).orElse(DEFAULT_COLOR))
                .lightColor(Optional.ofNullable(input.getLightColor()).orElse(DEFAULT_COLOR))
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

    public NutritionProgram updateNutritionProgram(Long programNumber, BaseNutritionProgramInput baseNutritionProgramInput) {
        validateBaseNutritionProgramInput(baseNutritionProgramInput);

        var program = getProgramOrElseThrow(programNumber);

        // required properties
        Optional.ofNullable(baseNutritionProgramInput.getName()).ifPresent(program::setName);
        Optional.ofNullable(baseNutritionProgramInput.getDescription()).ifPresent(program::setDescription);
        Optional.ofNullable(baseNutritionProgramInput.getShortDescription()).ifPresent(program::setShortDescription);

        // optional properties
        Optional.ofNullable(baseNutritionProgramInput.getAdditionalInfo()).ifPresent(program::setAdditionalInfo);
        Optional.ofNullable(baseNutritionProgramInput.getLightColor()).ifPresent(program::setLightColor);
        Optional.ofNullable(baseNutritionProgramInput.getDayColor()).ifPresent(program::setDayColor);
        Optional.ofNullable(baseNutritionProgramInput.getMainColor()).ifPresent(program::setMainColor);
        Optional.ofNullable(baseNutritionProgramInput.getImage()).ifPresent(baseNutritionProgramInput::setImage);

        return nutritionProgramRepository.save(program);
    }

    public Optional<NutritionProgram> findNutritionProgram(Long programNumber) {
        return nutritionProgramRepository.findById(programNumber);
    }

    private NutritionProgram getProgramOrElseThrow(Long programNumber) {
        return nutritionProgramRepository.findById(programNumber)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Nutrition Program with programNumber: %s does not exist", programNumber))
                );
    }

    public Long getTotalNumberOfAllPrograms() {
        return nutritionProgramRepository.count();
    }

    public Long getTotalNumberOfProgramsWithLanguage(Language language) {
        if (Language.isRussian(language)) {
            return nutritionProgramRepository.countAllByLanguage(language)
                    + nutritionProgramRepository.countAllByLanguage(null);
        }

        return nutritionProgramRepository.countAllByLanguage(language);
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
        var program = programStorageService.getProgramOrElseThrow(programNumber);
        return setStatusFor(program, ACCEPTED);
    }

    public NutritionProgram publishProgram(Long programNumber) {
        var program = programStorageService.getProgramOrElseThrow(programNumber);
        return setStatusFor(program, PUBLISHED);
    }

    public NutritionProgram revertProgram(Long programNumber) {
        var program = programStorageService.getProgramOrElseThrow(programNumber);
        return revertStatusFor(program);
    }

    public List<NutritionProgram> getLimitedNumberProgramsByKcal(
            Language language, Status status, Integer kcal, Integer maxNumber
    ) {
        Utils.validateVariableIsNonNegative(kcal, "kcal");

        if (status == null) {
            throw new IllegalArgumentException("Status can not be null");
        }

        var numberAbove = maxNumber / 2;
        var numberBelow = maxNumber - numberAbove;

        var programPageBelow = nutritionProgramRepository.findAllByLanguageAndStatusAndKcalGreaterThanOrderByKcalAsc(
                language, status, kcal, PageRequest.of(0, numberAbove)
        );
        var programPageAbove = nutritionProgramRepository.findAllByLanguageAndStatusAndKcalLessThanEqualOrderByKcalDesc(
                language, status, kcal, PageRequest.of(0, numberBelow)
        );

        var programs = new ArrayList<>(programPageAbove.toList());
        programs.addAll(programPageBelow.toList());

        return programs.stream()
                .sorted(getDefaultComparatorForSortingByKcal(kcal))
                .collect(Collectors.toList());
    }

    private Comparator<NutritionProgram> getDefaultComparatorForSortingByKcal(Integer targetKcal) {
        return Comparator.comparingInt(program -> Math.abs(program.getKcal() - targetKcal));
    }

    // todo: whether we need these methods. We should analyze potential user cases.

    public List<NutritionProgram> getPublishedPrograms(Language language, Integer kcal, Set<Lifestyle> lifestyles, ProductExclusion productExclusion) {
        return getLimitedNumberOfProgramsByKcalAndStatusAndLifestylesAndExcludedProducts(
                language, kcal, PUBLISHED, lifestyles, productExclusion, DEFAULT_MAX_NUMBER
        );
    }

    public List<NutritionProgram> getProgramsBy(
            Language language,
            Integer kcal,
            Integer deltaKcal,
            Status status,
            Set<Lifestyle> lifestyles,
            ProductExclusion productExclusion,
            Integer maxNumber
    ) {
        if (status == null) {
            status = PUBLISHED;
        }

        if (language == null) {
            language = Language.RUSSIAN;
        }

        if (maxNumber == null || maxNumber.equals(0)) {
            maxNumber = DEFAULT_MAX_NUMBER;
        }

        if (kcal != null && deltaKcal != null) {
            if (lifestyles == null || lifestyles.isEmpty() || productExclusion != null) {
                log.warn("Query parameters such as lifestyles and productExclusion will be ignored as kcal and deltaKcal are defined");
            }

            return getProgramsByKcal(status, kcal, deltaKcal);
        }

        if (kcal == null) {
            return nutritionProgramRepository.findAllByStatus(status);
        }

        if (lifestyles == null && productExclusion == null) {
            return getLimitedNumberProgramsByKcal(language, status, kcal, maxNumber);
        }

        if (lifestyles != null && productExclusion == null) {
            return getLimitedNumberOfProgramsByKcalAndStatusAndLifestyles(language, kcal, status, lifestyles, maxNumber);
        }

        if (lifestyles != null) {
            return getLimitedNumberOfProgramsByKcalAndStatusAndLifestylesAndExcludedProducts(
                    language, kcal, status, lifestyles, productExclusion, maxNumber
            );
        }

        // todo: find by status, lifestyles (?)

        throw new BadRequestException("Unsupported combination of input parameters");
    }

    private List<NutritionProgram> getProgramsByKcal(Status status, Integer targetKcal, Integer deltaKcal) {
        Utils.validateVariableIsNonNegative(deltaKcal, "deltaKcal");
        Utils.validateVariableIsNonNegative(targetKcal, "targetKcal");

        var minKcal = targetKcal - deltaKcal;
        var maxKcal = targetKcal + deltaKcal;

        if (minKcal < 0) {
            minKcal = 0;
        }

        return nutritionProgramRepository.findAllByStatusAndKcalIsBetween(status, minKcal, maxKcal);
    }

    private List<NutritionProgram> getLimitedNumberOfProgramsByKcalAndStatusAndLifestyles(
            Language language,
            Integer kcal,
            Status status,
            Set<Lifestyle> lifestyles,
            Integer maxNumber
    ) {
        Utils.validateVariableIsNonNegative(kcal, "kcal");

        if (status == null ) {
            throw new IllegalArgumentException("Status can not be null");
        }

        if (language == null) {
            throw new IllegalArgumentException("Language can not be null");
        }

        if (lifestyles == null || lifestyles.isEmpty()) {
            throw new IllegalArgumentException("Lifestyle can not be null or empty");
        }

        var pairOfProgramsPages = getProgramsPagesBy(language, kcal, status, lifestyles, maxNumber);

        return Stream.concat(pairOfProgramsPages.getFirst().stream(), pairOfProgramsPages.getSecond().stream())
                .sorted(getDefaultComparatorForSortingByKcal(kcal))
                .collect(Collectors.toList());
    }

    private Pair<Page<NutritionProgram>, Page<NutritionProgram>> getProgramsPagesBy(
            Language language,
            Integer kcal,
            Status status,
            Set<Lifestyle> lifestyles,
            Integer maxNumber
    ) {
        var numberAbove = maxNumber / 2;
        var numberBelow = maxNumber - numberAbove;

        var programsPageAbove = nutritionProgramRepository
                .findAllByLanguageAndStatusAndLifestylesInAndKcalGreaterThanOrderByKcalAsc(
                        language,
                        status,
                        lifestyles,
                        kcal,
                        PageRequest.of(0, numberAbove)
                );

        var programsPageBelow = nutritionProgramRepository
                .findAllByLanguageAndStatusAndLifestylesInAndKcalLessThanEqualOrderByKcalDesc(
                        language,
                        status,
                        lifestyles,
                        kcal,
                        PageRequest.of(0, numberBelow)
                );
        return Pair.of(programsPageAbove, programsPageBelow);
    }

    private List<NutritionProgram> getLimitedNumberOfProgramsByKcalAndStatusAndLifestylesAndExcludedProducts(
            Language language,
            Integer kcal,
            Status status,
            Set<Lifestyle> lifestyles,
            ProductExclusion productExclusion,
            Integer maxNumber
    ) {
        if (status == DRAFT) {
            throw new BadRequestException("Product exclusion is only available for query with status ACCEPTED or PUBLISHED");
        }

        var highPrograms = nutritionProgramRepository.findAllByLanguageAndStatusAndLifestylesInAndKcalGreaterThanOrderByKcalAsc(language, status, lifestyles, kcal);
        var lowPrograms = nutritionProgramRepository.findAllByLanguageAndStatusAndLifestylesInAndKcalLessThanEqualOrderByKcalDesc(language, status, lifestyles, kcal);

        var numberAbove = maxNumber / 2;
        var numberBelow = maxNumber - numberAbove;

        var filteredHighPrograms = highPrograms.filter(program -> shoppingListForProgramExcludesProductsIn(productExclusion, program))
                .limit(numberAbove)
                .collect(Collectors.toList());

        var filteredLowPrograms = lowPrograms.filter(program -> shoppingListForProgramExcludesProductsIn(productExclusion, program))
                .limit(numberBelow)
                .collect(Collectors.toList());

        return Stream.concat(filteredHighPrograms.stream(), filteredLowPrograms.stream())
                .sorted(getDefaultComparatorForSortingByKcal(kcal))
                .collect(Collectors.toList());
    }

    private boolean shoppingListForProgramExcludesProductsIn(ProductExclusion productExclusion, NutritionProgram program) {
        var shoppingList = shoppingListService.getShoppingListOrElseThrow(program.getNumber());

        var commonProductTypes = shoppingList.getAllProductTypes();
        commonProductTypes.retainAll(productExclusion.getTypes());

        if (!commonProductTypes.isEmpty()) {
            return false;
        }

        var commonProductNames = shoppingList.getAllProductNames();
        commonProductNames.retainAll(productExclusion.getNames());

        return commonProductNames.isEmpty();
    }

    public void deleteProgram(Long programNumber) {
        nutritionProgramRepository.deleteById(programNumber);
    }
}
