package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.context.annotation.DependsOn;

import javax.persistence.*;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = READ_ONLY)
    private Long     id;

    @Column(unique = true)
    private String   name;

    @ManyToOne
    private Image   image;
    private String  description;

    @ManyToMany//(cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;

    private Double totalKkal;
    private Double totalProteins;
    private Double totalFats;
    private Double totalCarbohydrates;

}
