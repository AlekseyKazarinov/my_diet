package com.mydiet.mydiet.domain.entity;

import lombok.*;

@Data
@AllArgsConstructor(staticName="of")
@NoArgsConstructor
@ToString
public class Quantity {

    private Double       totalQuantity;
    private QuantityUnit unit;

}
