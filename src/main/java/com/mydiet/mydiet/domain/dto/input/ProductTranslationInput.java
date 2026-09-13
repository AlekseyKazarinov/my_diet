package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.Language;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductTranslationInput {

    String name;
    Language language;

}
