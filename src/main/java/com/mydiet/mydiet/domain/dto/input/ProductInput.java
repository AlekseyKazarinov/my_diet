package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.ProductType;
import com.mydiet.mydiet.infrastructure.Consistence;
import lombok.Data;

import static com.mydiet.mydiet.infrastructure.Consistence.NOT_DEFINED;

@Data
public class ProductInput {

    private String   name;
    private Language language;

    private ProductType productType;
    private Consistence consistence = NOT_DEFINED;

}
