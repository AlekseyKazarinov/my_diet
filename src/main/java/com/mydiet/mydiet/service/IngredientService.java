package com.mydiet.mydiet.service;

import com.google.common.base.Preconditions;
import com.mydiet.mydiet.domain.dto.IngredientCreationInput;
import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.domain.entity.QuantityUnit;
import com.mydiet.mydiet.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final ProductService       productService;
    private final IngredientRepository ingredientRepository;

    public void validateIngredientCreationInput(IngredientCreationInput ingredient) {
        Preconditions.checkNotNull(ingredient, "Ingredient is null");

        Utils.validateValueIsNonNegative(ingredient.getTotalQuantity(), ingredient);
        QuantityUnit.validateUnit(ingredient.getUnit());
    }

    public Ingredient createIngredient(IngredientCreationInput ingredientCreationInput) {
        validateIngredientCreationInput(ingredientCreationInput);

        var product = productService.createProduct(ingredientCreationInput.getProduct());

        var ingredient = Ingredient.builder()
                .product(product)
                .unit(QuantityUnit.of(ingredientCreationInput.getUnit()))
                .totalQuantity(ingredientCreationInput.getTotalQuantity())
                .build();

        return ingredientRepository.save(ingredient);
    }

}
