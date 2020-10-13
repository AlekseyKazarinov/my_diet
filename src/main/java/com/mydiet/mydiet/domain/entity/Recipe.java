package com.mydiet.mydiet.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long     id;

    private String   name;
    private String   imageId;
    private String   description;

    @OneToMany
    private List<Ingredient> ingredients;

    @Transient
    private Double totalKkal;
    @Transient
    private Double totalProteins;
    @Transient
    private Double totalFats;
    @Transient
    private Double totalCarbohydrates;

}
