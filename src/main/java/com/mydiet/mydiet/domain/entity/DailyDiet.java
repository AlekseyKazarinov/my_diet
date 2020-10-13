package com.mydiet.mydiet.domain.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
@Builder
public class DailyDiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    @MapKeyJoinColumn(name = "food_time")
    private Map<FoodTime, Meal> mealByTime;

    private Integer dayNumber;

}
