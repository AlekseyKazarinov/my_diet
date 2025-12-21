package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

import java.util.Optional;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Entity
@Table(name = "INGREDIENT")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = READ_ONLY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;
    private Double totalQuantity;
    private QuantityUnit unit;

    @JsonIgnore
    @ManyToMany(mappedBy = "ingredients")
    private Set<Recipe> relatedRecipes;
/*
    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", productId=" + (product == null ? null : product.getName()) +
                ", totalQuantity=" + totalQuantity +
                ", unit=" + unit +
                '}';
    }*/
}
