package com.mydiet.mydiet.domain.dto.output.android;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ingredient {

    String product;
    Double totalQuantity;
    String unit;

}
