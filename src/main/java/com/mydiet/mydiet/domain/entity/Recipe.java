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

    @ManyToOne
    private Image   image;
    private String  description;

    @OneToMany
    private List<Ingredient> ingredients;

    private Double totalKkal;
    private Double totalProteins;
    private Double totalFats;
    private Double totalCarbohydrates;

}
