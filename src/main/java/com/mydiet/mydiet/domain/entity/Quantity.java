package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class Quantity {

    private Double       totalQuantity;
    private QuantityUnit unit;

}