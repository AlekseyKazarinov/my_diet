package com.mydiet.mydiet.domain.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Meal {

    @Id
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Recipe recipe;

    private FoodTime foodTime;

    /*private Integer dayNumber;
    private Integer programNumber;*/

}
