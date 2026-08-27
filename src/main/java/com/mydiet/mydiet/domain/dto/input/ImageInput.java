package com.mydiet.mydiet.domain.dto.input;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageInput {

    String name;
    String resource;
    
}
