package com.mydiet.mydiet.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.DayOfWeek;

@Data
@EqualsAndHashCode(callSuper = true)
public class DailyShoppingList extends ShoppingList {

    private DayOfWeek day;
    private Integer serialNumber;

}
