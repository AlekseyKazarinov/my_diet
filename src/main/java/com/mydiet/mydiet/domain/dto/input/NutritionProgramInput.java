package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.dto.input.DailyDietInput;
import com.mydiet.mydiet.domain.entity.Lifestyle;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class NutritionProgramInput {

    private String name;
    private String description;
    private String additionalInfo;

    private Set<Lifestyle> lifestyles;

    private String image;
    private String backgroundColour;

    private List<Long> dailyDietIds;
    private Short      dailyNumberOfMeals;

}
