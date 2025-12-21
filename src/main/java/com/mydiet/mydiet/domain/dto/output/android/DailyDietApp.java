package com.mydiet.mydiet.domain.dto.output.android;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DailyDietApp {

    public Long id;
    public String name;
    public List<MealIdContainer> meals;

}
