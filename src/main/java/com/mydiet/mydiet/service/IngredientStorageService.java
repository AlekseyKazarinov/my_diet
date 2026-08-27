package com.mydiet.mydiet.service;


import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientStorageService {

    private final IngredientRepository ingredientRepository;

    public Ingredient saveIfOriginal(Ingredient ingredient) {
        var optionalStoredIngredient = ingredientRepository.findByProductAndTotalQuantityAndUnit(
                ingredient.getProduct(), ingredient.getTotalQuantity(), ingredient.getUnit()
        );

        if (optionalStoredIngredient.isPresent()) {
            log.info("ingredient {} is already exist", ingredient.getProduct());
            return optionalStoredIngredient.get();
        }

        return ingredientRepository.save(ingredient);
    }

}
