package com.mydiet.mydiet.domain.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public abstract class ShoppingList {

    private Map<Product, Quantity> listOfProducts;

}
