package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.dto.input.DailyDietInput;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NutritionProgramInput {

    private String name;
    private String description;
    private String additionalInfo;

    private String image;
    private String backgroundColour;

    private List<DailyDietInput> dailyDietInputs;
    private Short                dailyNumberOfMeals;

}
