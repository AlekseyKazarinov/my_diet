package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.dto.input.IngredientInput;
import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.repository.IngredientRepository;
import com.mydiet.mydiet.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredients")
public class IngredientController {

    private final IngredientService ingredientService;
    private final IngredientRepository ingredientRepository;

    @Operation(summary = "Update an ingredient")
    @PutMapping("/{ingredientId}/update")
    ResponseEntity<Ingredient> updateIngredient(
            @PathVariable @NonNull Long ingredientId,
            @RequestBody @NonNull IngredientInput ingredientUpdateInput
    ) {
        var ingredient = ingredientService.updateValidatedIngredient(ingredientId, ingredientUpdateInput);
        return ResponseEntity.ok(ingredient);
    }

    @DeleteMapping("/{ingredientId}")
    @Operation(summary = "This endpoint is not intended for regular using", description = "API provides such a function just in case. " +
            "Regular using may cause inconsistency between all basic entities making work unstable")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long ingredientId) {
        ingredientRepository.deleteById(ingredientId);
        return ResponseEntity.noContent().build();
    }
}
