package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.entity.Image;
import com.mydiet.mydiet.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public Image createValidatedImage(String name, String resource) {
        Utils.validateTextFieldIsSet(resource, "resource");
        var image = Image.builder()
                .name(name)
                .resource(resource)
                .build();

        return saveImage(image);
    }

    private Image saveImage(Image image) {
        var optionalImage = imageRepository.findImageByName(image.getName());
        return optionalImage.orElseGet(() -> imageRepository.save(image));
    }

}
