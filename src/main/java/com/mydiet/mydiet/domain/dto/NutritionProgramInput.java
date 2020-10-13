package com.mydiet.mydiet.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NutritionProgramInput {

    private Integer number;
    private String  name;
    private String  description;

}
