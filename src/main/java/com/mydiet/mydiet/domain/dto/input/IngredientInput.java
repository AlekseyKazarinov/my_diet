package com.mydiet.mydiet.domain.dto.input;

import lombok.Data;

@Data
public class IngredientInput {

    private ProductInput product;

    private Double totalQuantity;
    private String unit;

}
