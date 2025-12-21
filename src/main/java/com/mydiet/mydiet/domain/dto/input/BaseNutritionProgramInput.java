package com.mydiet.mydiet.domain.dto.input;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class BaseNutritionProgramInput {

    private String name;
    private String shortDescription;
    private String description;
    private String additionalInfo;

    private String image;
    //private String backgroundColour; // todo: delete this field?

    private String dayColor;
    private String mainColor;
    private String lightColor;
}
