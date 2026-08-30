package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.entity.Image;
import com.mydiet.mydiet.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor
public class ImageServiceTest {

    private final String IMAGE_NAME = "unique image name";
    private final String IMAGE_CONTENT = "base64-encoded image resource";

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    @Test
    public void testCreateValidatedImage() {
        // Given
        when(imageRepository.findImageByName(any())).thenReturn(Optional.empty());
        when(imageRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // When
        var createdImage = imageService.createValidatedImage(IMAGE_NAME, IMAGE_CONTENT);

        // Then
        verify(imageRepository, times(1)).findImageByName(any());
        Assertions.assertNotNull(createdImage);
        Assertions.assertEquals(IMAGE_NAME, createdImage.getName());
        Assertions.assertEquals(IMAGE_CONTENT, createdImage.getResource());
    }


}
