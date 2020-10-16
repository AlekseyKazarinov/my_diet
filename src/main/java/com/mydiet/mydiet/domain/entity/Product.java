package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(unique = true)
    private String name;

    @JsonIgnore
    private Double kkalPer100g;
    @JsonIgnore
    private Double proteins;
    @JsonIgnore
    private Double fats;
    @JsonIgnore
    private Double carbohydrates;

    private ProductType productType;

}
