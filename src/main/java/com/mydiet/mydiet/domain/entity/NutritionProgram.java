package com.mydiet.mydiet.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Data
public class NutritionProgram {

    @Id
    @Column(unique = true)
    private Integer number;       // preview

    @Version
    private Long version;

    @LastModifiedDate
    private Instant lastModifiedAt;

    private String name;          // preview

    @ManyToOne
    private Image imageProgram;  // preview
    private String description;   // preview   // todo: stub for language: default = Russian

    private String backgroundColour;  // preview

    @OneToMany
    private List<DailyDiet> dailyDietList;  // as a user I want to ..?

    private Integer numberOfMeals;

}
