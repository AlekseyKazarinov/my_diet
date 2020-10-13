package com.mydiet.mydiet.domain.entity;

import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class NutritionProgram {

    @Id
    @Column(unique = true)
    private Integer number;

    private String  name;
    private String  description;

    @OneToMany
    private List<DailyDiet> dailyDietList;

    @Formula("round(numberOfDays / numberOfWeeks)")  //todo: it works?
    private Integer numberOfWeeks;
    private Integer numberOfDays;

}
