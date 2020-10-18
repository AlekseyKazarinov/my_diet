package com.mydiet.mydiet.service;

import com.google.common.base.Preconditions;
import com.mydiet.mydiet.domain.dto.IngredientCreationInput;
import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.domain.entity.QuantityUnit;
import com.mydiet.mydiet.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientService {

    private final ProductService       productService;
    private final IngredientRepository ingredientRepository;

    public void validateIngredientCreationInput(IngredientCreationInput ingredient) {
        Preconditions.checkNotNull(ingredient, "Ingredient is null");

        Utils.validateValueIsNonNegative(ingredient.getTotalQuantity(), ingredient);
        QuantityUnit.validateUnit(ingredient.getUnit());

        productService.validateProductCreationInput(ingredient.getProduct());
    }

    public Ingredient createIngredient(IngredientCreationInput ingredientCreationInput) {
        var product = productService.createProduct(ingredientCreationInput.getProduct());

        var ingredient = Ingredient.builder()
                .product(product)
                .unit(QuantityUnit.of(ingredientCreationInput.getUnit()))
                .totalQuantity(ingredientCreationInput.getTotalQuantity())
                .build();
        //return ingredient;
        return saveIngredient(ingredient);
    }

    private Ingredient saveIngredient(Ingredient ingredient) {
        var optionalStoredIngredient = ingredientRepository.findByProductAndTotalQuantityAndUnit(
                ingredient.getProduct(), ingredient.getTotalQuantity(), ingredient.getUnit()
        );

        if (optionalStoredIngredient.isPresent()) {
            log.info("ingredient {} is already exist", ingredient.getProduct());
            return optionalStoredIngredient.get();
        }
        return ingredientRepository.save(ingredient);
    }

    public Ingredient createValidatedIngredient(IngredientCreationInput ingredientCreationInput) {
        validateIngredientCreationInput(ingredientCreationInput);
        return createIngredient(ingredientCreationInput);
    }

}
