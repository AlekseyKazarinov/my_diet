package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Entity
@Table(name = "DAILY_DIET")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyDiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = READ_ONLY)
    private Long id;

    private String name;

    @ManyToMany//(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "DAILY_DIET_MEAL",
            joinColumns = @JoinColumn(
                    name = "DAILY_DIET_ID", referencedColumnName = "ID"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "MEAL_ID", referencedColumnName = "ID"
            )
    )
    private Set<Meal> meals;

    @ElementCollection
    private Set<Lifestyle> lifestyles;

    @JsonIgnore
    @ManyToMany(mappedBy = "dailyDiets")
    private Set<NutritionProgram> relatedPrograms;

}
