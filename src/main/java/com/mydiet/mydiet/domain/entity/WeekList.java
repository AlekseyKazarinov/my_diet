package com.mydiet.mydiet.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
public class WeekList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer                            numberOfWeek;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @MapKeyEnumerated(value = EnumType.STRING)
    //@MapKeyJoinColumn
    private Map<ProductType, ProductRowsForType> productListsByType = new HashMap<>();

    /*public void addProductRow(ProductRow productRow) {
        var productType = productRow.getProduct().getProductType();

        if (!listsByProductType.containsKey(productType)) {
            listsByProductType.put(productType, new ArrayList<>());
        }

        listsByProductType.get(productType).add(productRow);
    }*/

}
