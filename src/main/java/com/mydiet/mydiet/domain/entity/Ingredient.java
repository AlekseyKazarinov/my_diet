package com.mydiet.mydiet.domain.entity;

import javax.persistence.*;

@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;
    private Double totalQuantity;
    private QuantityUnit unit;

}
