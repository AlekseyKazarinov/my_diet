package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class ProductRow {

    @JsonIgnore
    private ProductType  productType;

    private String       productName;
    private Double       totalQuantity;
    private QuantityUnit unit;

}
