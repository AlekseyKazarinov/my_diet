package com.mydiet.mydiet.domain.dto.output;

import com.mydiet.mydiet.domain.entity.Image;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class NutritionProgramOutput {
    private Long number;

    private Long version;

    private Instant lastModifiedAt;

    private String name;
    private String description;
    private String additionalInfo;

    private Image image;
    private String backgroundColour;

    private Short numberOfMeals;

    private List<DailyDietOutput> dailyDietList;

}
