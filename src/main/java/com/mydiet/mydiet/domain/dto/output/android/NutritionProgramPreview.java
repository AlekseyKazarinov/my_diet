package com.mydiet.mydiet.domain.dto.output.android;

import com.mydiet.mydiet.domain.entity.Image;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NutritionProgramPreview {

    private Long   number;

    private String name;
    private String shortDescription;

    private Image  image;
    private String lightColor;

}
