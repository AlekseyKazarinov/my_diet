package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collections;
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

    //@JsonProperty(access = READ_ONLY)  - commented. This property will be read-only in entity representing the program in the api for android applications
    private Status status;

    //@Index(...)
    private String   langId;     // grouping by the same context  todo: use
    private Language language;

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


    public Integer getNumberOfWeeks() {
        if (this.getDailyDiets() == null) {
            return null;
        }

        var numberOfDays = this.getDailyDiets().size();
        var fullWeeks = numberOfDays / 7;
        var remainder = numberOfDays % 7;

        if (remainder == 0) {
            return fullWeeks;
        } else {
            return ++fullWeeks;
        }
    }

    public List<DailyDiet> getDailyDietsForWeekNo(Integer weekNumber) {
        Assert.notNull(weekNumber, "weekNumber must not be null");

        if (weekNumber > this.getNumberOfWeeks()) {
            return Collections.emptyList();
        }

        var dailyDiets = this.getDailyDiets();
        var firstDay = 7 * (weekNumber - 1);
        var lastDay = Math.min( 7 * weekNumber, dailyDiets.size());

        return dailyDiets.subList(firstDay, lastDay);
    }

}
