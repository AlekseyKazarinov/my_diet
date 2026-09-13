package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.QuantityUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class IngredientInput {

    private ProductInput product;

    private Double totalQuantity;
    private QuantityUnit unit;

}
