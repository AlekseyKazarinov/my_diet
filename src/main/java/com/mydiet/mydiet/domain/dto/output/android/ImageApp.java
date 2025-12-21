package com.mydiet.mydiet.domain.dto.output.android;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageApp {

    public Long id;
    public String name;
    public String resource;

    public static ImageApp from(com.mydiet.mydiet.domain.entity.Image image) {
        return ImageApp.builder()
                .id(image.getId())
                .name(image.getName())
                .resource(image.getResource())
                .build();
    }

}
