package com.mydiet.mydiet.domain.dto;

import lombok.Data;

@Data
public class IngredientCreationInput {

    private ProductCreationInput product;

    private Double               totalQuantity;
    private String               unit;

}
