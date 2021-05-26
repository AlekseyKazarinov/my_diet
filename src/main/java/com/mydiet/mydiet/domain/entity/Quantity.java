package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@AllArgsConstructor(staticName="of")
@ToString
public class Quantity {

    private Double       totalQuantity;
    private QuantityUnit unit;

}
