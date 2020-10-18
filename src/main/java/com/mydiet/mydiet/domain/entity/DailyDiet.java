package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyDiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = READ_ONLY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    /*@JoinTable(
            name = "daily_diet_meal",
            joinColumns = @JoinColumn(
                    name = "daily_diet_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "meal_id"
            )
    )*/
    private List<Meal> mealList;

    private int numberOfMeals;

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
    }

}
