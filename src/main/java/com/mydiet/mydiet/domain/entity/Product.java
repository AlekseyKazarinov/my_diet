package com.mydiet.mydiet.domain.entity;

import javax.persistence.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private Double kkalPer100g;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;

    private ProductType productType;

}
