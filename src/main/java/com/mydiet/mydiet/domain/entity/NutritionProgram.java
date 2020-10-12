package com.mydiet.mydiet.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class NutritionProgram {

    @Id
    @Column(unique = true)
    private Integer number;

    private List<Recipe> recipes;

    //private Integer numberOfWeeks;
    //private Integer numberOfDays;

}
