package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Entity
@Table(name = "NUTRITION_PROGRAM")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    @JsonProperty(access = READ_ONLY)
    private Long number;       // preview

    @Version
    @JsonProperty(access = READ_ONLY)
    private Long version;

    @LastModifiedDate
    @JsonProperty(access = READ_ONLY)
    private Instant lastModifiedAt;

    @JsonProperty(access = READ_ONLY)
    private Status status;

    private String name;             // preview
    private String shortDescription; // preview
    private String description;      // todo: stub for language: default = Russian
    private String additionalInfo;   // optional

    private Integer kcal;

    @ElementCollection
    private Set<Lifestyle> lifestyles;

    @ManyToOne
    private Image image;             // preview

    private String dayColor;     // dayColour
    private String mainColor;    // main color?
    private String lightColor;   // background color?   preview

    @ManyToMany
    private List<DailyDiet> dailyDiets;  // as a user I want to ..?

    private Short dailyNumberOfMeals;

}
