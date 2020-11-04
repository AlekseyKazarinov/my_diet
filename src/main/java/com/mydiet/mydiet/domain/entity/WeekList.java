package com.mydiet.mydiet.domain.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class WeekList {

    private Integer                            numberOfWeek;
    private Map<ProductType, List<ProductRow>> listsByProductType = new HashMap<>();

    public void addProductRow(ProductRow productRow) {
        var productType = productRow.getProduct().getProductType();

        if (!listsByProductType.containsKey(productType)) {
            listsByProductType.put(productType, new ArrayList<>());
        }

        listsByProductType.get(productType).add(productRow);
    }

}
