package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRow {

    private Product      product;
    private Double       totalQuantity;
    private QuantityUnit unit;

}
