package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.springframework.context.annotation.DependsOn;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Entity
@Table(name = "RECIPE")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = READ_ONLY)
    private Long     id;

    @Column(unique = true)
    private String   name;
    private Language language;

    @ManyToOne
    private Image   image;
    private String  description;
    private FoodCategory foodCategory;

    @ElementCollection  // https://www.baeldung.com/jpa-tagging https://www.baeldung.com/jpa-tagging-advanced
    private Set<Lifestyle> lifestyles;

    @ManyToMany//(cascade = CascadeType.ALL)
    @JoinTable(name = "RECIPE_INGREDIENT",
            joinColumns=@JoinColumn(name = "RECIPE_ID", referencedColumnName = "ID"),
            inverseJoinColumns=@JoinColumn(name = "INGREDIENT_ID", referencedColumnName = "ID"))
    private List<Ingredient> ingredients;

    private Double totalKcal;
    private Double totalProteins;
    private Double totalFats;
    private Double totalCarbohydrates;

    @JsonIgnore
    @OneToMany(mappedBy = "recipe")
    private Set<Meal> relatedMeals;

}
