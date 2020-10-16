package com.mydiet.mydiet.domain.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Meal {

    @Id
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Recipe recipe;

    private FoodTime foodTime;

}
