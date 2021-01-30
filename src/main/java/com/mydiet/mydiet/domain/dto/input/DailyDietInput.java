package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.Lifestyle;
import lombok.Data;

import java.util.Set;

@Data
public class DailyDietInput {

    private String    name;
    private Set<Long> mealIds;
    private Set<Lifestyle> lifestyles;

}
