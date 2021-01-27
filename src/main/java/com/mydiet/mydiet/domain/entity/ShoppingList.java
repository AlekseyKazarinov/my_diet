package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import java.util.List;

@Data
@NoArgsConstructor
public class ShoppingList {

    @Id
    private Long nutritionProgramNumber;
    private List<WeekList> listsByWeek;

    @OneToOne
    @MapsId
    @JsonIgnore
    private NutritionProgram program;

}
