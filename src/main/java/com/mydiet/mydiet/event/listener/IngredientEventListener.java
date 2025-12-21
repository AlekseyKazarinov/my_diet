package com.mydiet.mydiet.event.listener;

import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.domain.entity.NutritionProgram;
import com.mydiet.mydiet.event.DomainEvent;
import com.mydiet.mydiet.event.SourceEntity;
import com.mydiet.mydiet.service.IngredientService;
import com.mydiet.mydiet.service.NutritionProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import static com.mydiet.mydiet.domain.entity.Status.DRAFT;


@Slf4j
@Component
@RequiredArgsConstructor
public class IngredientEventListener extends DomainEventListener {

    private final IngredientService ingredientService;
    private final NutritionProgramService nutritionProgramService;

    @Override
    boolean isAcceptable(DomainEvent event) {
        return SourceEntity.INGREDIENT.equals(event.getSourceEntityType());
    }

    @Override
    @Transactional
    void handle(DomainEvent event) {
        var ingredientId = event.getSourceId();
        var ingredient = ingredientService.getIngredientOrThrow(ingredientId);
        log.info("Ingredient #{}: {} was changed", ingredientId, ingredient.getProduct().getName());

        var affectedProgramsIterator = getAffectedPrograms(ingredient);

        affectedProgramsIterator.forEachRemaining(program -> nutritionProgramService.setStatusFor(program, DRAFT));
    }
    
    private Iterator<NutritionProgram> getAffectedPrograms(Ingredient ingredient) {
        return ingredient.getRelatedRecipes().stream()
                .flatMap(recipe -> Optional.ofNullable(recipe.getRelatedMeals())
                        .orElse(Collections.emptySet())
                        .stream())
                .distinct()
                .flatMap(meal -> Optional.ofNullable(meal.getRelatedDailyDiets())
                        .orElse(Collections.emptySet())
                        .stream())
                .distinct()
                .flatMap(dailyDiet -> Optional.ofNullable(dailyDiet.getRelatedPrograms())
                        .orElse(Collections.emptySet())
                        .stream())
                .iterator();
    }
}
