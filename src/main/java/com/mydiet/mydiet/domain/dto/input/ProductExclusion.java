package com.mydiet.mydiet.domain.dto.input;

import com.mydiet.mydiet.domain.entity.ProductType;
import lombok.Data;

import java.util.List;

@Data
public class ProductExclusion {

    private List<ProductType> types;
    private List<String> names;

}
