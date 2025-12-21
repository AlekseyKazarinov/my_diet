package com.mydiet.mydiet.domain.dto.output;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class RecipeOutput {

    private Long         id;
    private String       name;
    private String       image;
    private String       description;
    private List<String> ingredients;
    private Integer      kkal;
    private String       foodTime;
    private String       day;
    private Long         programNumber;

}
