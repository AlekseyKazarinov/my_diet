package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCT_ROW")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRowsForType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<ProductRow> productRows = new ArrayList<>();

    public ProductRowsForType(List<ProductRow> productRows) {
        if (productRows == null || productRows.isEmpty()) {
            this.productRows = new ArrayList<>();

        } else {
            this.productRows = productRows;
        }
    }
}
