package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.Language;
import lombok.Data;

@Data
public class ProgramTranslationInput {

    String name;
    String shortDescription;
    String description;
    String additionalInfo;

    Language language;

}
