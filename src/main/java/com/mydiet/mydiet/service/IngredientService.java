package com.mydiet.mydiet.service;

import com.google.common.base.Preconditions;
import com.mydiet.mydiet.domain.dto.input.IngredientInput;
import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.domain.entity.QuantityUnit;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientService {

    private final ProductService       productService;
    private final IngredientRepository ingredientRepository;

    public void validateIngredientCreationInput(IngredientInput ingredient) {
        Preconditions.checkNotNull(ingredient, "Ingredient is null");

        Utils.validateFieldIsNonNegative(ingredient.getTotalQuantity(), "TotalQuantity", ingredient);
        QuantityUnit.validateUnit(ingredient.getUnit());

        productService.validateProductInput(ingredient.getProduct());
    }

    public Ingredient createIngredient(IngredientInput ingredientCreationInput) {
        var product = productService.createProduct(ingredientCreationInput.getProduct());

        var ingredient = Ingredient.builder()
                .product(product)
                .unit(QuantityUnit.of(ingredientCreationInput.getUnit()))
                .totalQuantity(ingredientCreationInput.getTotalQuantity())
                .build();

        //return ingredient;
        return saveIfOriginal(ingredient);
    }

    private Ingredient saveIfOriginal(Ingredient ingredient) {
        var optionalStoredIngredient = ingredientRepository.findByProductAndTotalQuantityAndUnit(
                ingredient.getProduct(), ingredient.getTotalQuantity(), ingredient.getUnit()
        );

        if (optionalStoredIngredient.isPresent()) {
            log.info("ingredient {} is already exist", ingredient.getProduct());
            return optionalStoredIngredient.get();
        }

        return ingredientRepository.save(ingredient);
    }

    public Ingredient getIngredientOrThrow(Long ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .orElseThrow(
                    () -> new NotFoundException(String.format("Ingredient with id: %s does not exist", ingredientId))
                );
    }

    public Ingredient updateIngredient(Long ingredientId, IngredientInput ingredientUpdateInput) {
        var ingredient = getIngredientOrThrow(ingredientId);

        productService.updateProduct(ingredient.getProduct().getId(), ingredientUpdateInput.getProduct());
        ingredient.setTotalQuantity(ingredientUpdateInput.getTotalQuantity());
        ingredient.setUnit(QuantityUnit.of(ingredientUpdateInput.getUnit()));

        return ingredientRepository.save(ingredient);
    }

    public Ingredient createValidatedIngredient(IngredientInput ingredientCreationInput) {
        validateIngredientCreationInput(ingredientCreationInput);
        return createIngredient(ingredientCreationInput);
    }

    public void removeIngredientsWithIds(List<Long> ingredientIds) {
        if (ingredientIds.isEmpty()) {
            return;
        }
        ingredientRepository.deleteByIdIn(ingredientIds);
    }

}
