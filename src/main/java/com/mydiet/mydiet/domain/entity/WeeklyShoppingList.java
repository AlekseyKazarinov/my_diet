package com.mydiet.mydiet.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeeklyShoppingList extends ShoppingList {

    private Integer weekNumber;

}
