package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.QuantityUnit;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngredientInput {

    private ProductInput product;

    private Double totalQuantity;
    private QuantityUnit unit;

}
