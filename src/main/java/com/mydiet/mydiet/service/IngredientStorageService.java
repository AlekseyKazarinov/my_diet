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
        //var totalQuantity = ingredient.getQuantity() == null ? null : ingredient.getQuantity().getTotalQuantity();
        //var unit = ingredient.getQuantity() == null ? null : ingredient.getQuantity().getUnit();

        var optionalStoredIngredient = ingredientRepository.findByProductAndQuantity(
                ingredient.getProduct(), ingredient.getQuantity()
        );

        if (optionalStoredIngredient.isPresent()) {
            log.info("ingredient {} is already exist", ingredient.getProduct());
            return optionalStoredIngredient.get();
        }

        return ingredientRepository.save(ingredient);
    }

}
