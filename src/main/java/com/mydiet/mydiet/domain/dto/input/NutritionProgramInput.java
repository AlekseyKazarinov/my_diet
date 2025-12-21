package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.Lifestyle;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Data
@SuperBuilder
public class NutritionProgramInput extends BaseNutritionProgramInput {

    private Language language;

    private Set<Lifestyle> lifestyles;

    private List<Long> dailyDietIds;
    private Short      dailyNumberOfMeals;

}
