package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.input.ImageCreationInput;
import com.mydiet.mydiet.domain.entity.Image;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public Image createValidatedImage(String name, String resource) {
        Utils.validateTextVariableIsSet(resource, "resource");
        Utils.validateTextVariableIsSet(name, "name");
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

    public Image getImageOrThrow(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Product with id: %s does not exist", imageId))
                );
    }

    public Image updateImage(Long imageId, ImageCreationInput imageInput) {
        var image = getImageOrThrow(imageId);

        Utils.validateStringFieldIsSet(imageInput.getResource(), "resource", imageInput);
        Utils.validateStringFieldIsSet(imageInput.getName(), "name", imageInput);

        image.setName(imageInput.getName());
        image.setResource(imageInput.getResource());

        return saveImage(image);
    }

    public Image mapToImage(ImageCreationInput imageInput) {
        Utils.validateStringFieldIsSet(imageInput.getResource(), "resource", imageInput);
        return Image.builder()
                .name(imageInput.getName())
                .resource(imageInput.getResource())
                .build();
    }

}
