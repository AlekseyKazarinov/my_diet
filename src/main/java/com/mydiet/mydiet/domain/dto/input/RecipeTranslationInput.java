package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.Language;
import lombok.Data;

@Data
public class RecipeTranslationInput {

    String name;
    String description;

    Language language;

}
