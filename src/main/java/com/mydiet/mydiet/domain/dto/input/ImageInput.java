package com.mydiet.mydiet.domain.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ImageInput {

    String name;
    String resource;
    
}
