package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.Language;
import lombok.Data;

@Data
public class ProductInput {

    private String   name;
    private Language language;

    private String productType;
    private String consistence;

}
