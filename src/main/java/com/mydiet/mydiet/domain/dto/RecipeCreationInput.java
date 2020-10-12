package com.mydiet.mydiet.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecipeCreationInput {

    private String name;
    private String description;
    private String foodTime;
    private String day;
    private Integer programNumber;

}
