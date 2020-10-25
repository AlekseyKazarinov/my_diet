package com.mydiet.mydiet.domain.entity;

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

    //private int numberOfMeals;
/*
    public void addMeal(Meal meal) {
        if (mealList.size() == numberOfMeals) {

            throw new IndexOutOfBoundsException("Daily Diet " + id + " already has " + numberOfMeals + " meals");
        }

        mealList.add(meal);
        numberOfMeals++;
    }

    public void removeMeal(int index) {
        mealList.remove(index);
        numberOfMeals--;
    }

    public void removeMealFor(FoodTime foodTime) {
        var removed = mealList.removeIf(meal -> meal.getFoodTime() == foodTime);

        if (removed) {
            numberOfMeals--;
        }
    }*/

}
