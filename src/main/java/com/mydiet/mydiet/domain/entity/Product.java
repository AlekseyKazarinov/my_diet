package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mydiet.mydiet.infrastructure.Consistence;
import lombok.*;

import javax.persistence.*;

import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Entity
@Table(name = "PRODUCT")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "ingredients")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = READ_ONLY)
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

    @JsonProperty(access = WRITE_ONLY)
    private Consistence consistence;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product", orphanRemoval = false)
    private Set<Ingredient> relatedIngredients;

}
