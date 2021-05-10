package com.mydiet.mydiet.domain.dto.output.android;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class NutritionProgramApp {

    public Long number;

    public String lastModifiedAt;
    public Long version;

    public ImageIdContainer image;


    // colour
    private String dayColor;
    private String mainColor;
    private String lightColor;


    // text
    private String   langId;
    private String language;

    public String name;
    public String description;
    private String shortDescription;
    private String additionalInfo;


    // content
    public List<DailyDietIdContainer> dailyDiets;
    public Short dailyNumberOfMeals;

    private Integer kcal;

    private Set<String> lifestyles;

}