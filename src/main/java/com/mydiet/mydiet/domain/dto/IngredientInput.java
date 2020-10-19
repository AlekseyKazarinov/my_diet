package com.mydiet.mydiet.domain.dto;

import lombok.Data;

@Data
public class IngredientInput {

    private ProductInput product;

    private Double               totalQuantity;
    private String               unit;

}
