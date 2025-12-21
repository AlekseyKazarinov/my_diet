package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "PRODUCT_ROW")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    private ProductType  productType;

    private String       productName;
    private Double       totalQuantity;
    private QuantityUnit unit;

}
