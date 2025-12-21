package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Entity
@Table(name = "MEAL")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = READ_ONLY)
    private Long id;

    @ManyToOne//(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Recipe recipe;

    private FoodTime foodTime;

    @JsonIgnore
    @ManyToMany(mappedBy = "meals")
    private Set<DailyDiet> relatedDailyDiets;

}
