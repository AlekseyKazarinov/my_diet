package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.dto.input.IngredientInput;
import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.service.IngredientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
@Api(tags = "Ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    @ApiOperation("Update an ingredient")
    @PutMapping("/{ingredientId}/update")
    ResponseEntity<Ingredient> updateIngredient(
            @PathVariable @NonNull Long ingredientId,
            @RequestBody @NonNull IngredientInput ingredientUpdateInput
    ) {
        var ingredient = ingredientService.updateValidatedIngredient(ingredientId, ingredientUpdateInput);
        return ResponseEntity.ok(ingredient);
    }


    // todo: delete Ingredient

}
