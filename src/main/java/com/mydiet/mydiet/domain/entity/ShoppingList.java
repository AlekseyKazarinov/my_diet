package com.mydiet.mydiet.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ShoppingList {

    private Long nutritionProgramNumber;
    private List<WeekList> listsByWeek;

}
