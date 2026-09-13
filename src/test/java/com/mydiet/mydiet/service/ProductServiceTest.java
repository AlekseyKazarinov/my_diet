package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.dto.input.ProductTranslationInput;
import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.ProductType;
import com.mydiet.mydiet.infrastructure.Consistence;
import com.mydiet.mydiet.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProductServiceTest {

    private final Long PRODUCT_ID = 1L;
    private final String LANG_GROUP_ID = UUID.randomUUID().toString();
    private final String TOMATO_NAME = "Tomato";

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    // add product translation

    @Test
    public void translateProduct() {
        // Given
        when(productRepository.findById(eq(PRODUCT_ID)))
                .thenReturn(Optional.of(getOriginalProduct()));
        when(productRepository.findProductByLangGroupIdAndLanguage(eq(LANG_GROUP_ID), eq(Language.ENGLISH)))
                .thenReturn(Optional.empty());
        when(productRepository.save(any())).then(AdditionalAnswers.returnsFirstArg());
        when(productRepository.findProductByName(eq(TOMATO_NAME))).thenReturn(Optional.empty());

        var productTranslationInput = getProductTranslationInput();

        // When
        var translatedProduct = productService.translateProduct(PRODUCT_ID, productTranslationInput);

        //Then
        Assertions.assertNotNull(translatedProduct);
        Assertions.assertEquals(Language.ENGLISH, translatedProduct.getLanguage());
        Assertions.assertEquals(LANG_GROUP_ID, translatedProduct.getLangGroupId());
        Assertions.assertEquals(TOMATO_NAME, translatedProduct.getName());

        Assertions.assertEquals(ProductType.VEGETABLE, translatedProduct.getProductType());
        Assertions.assertEquals(Consistence.SOLID, translatedProduct.getConsistence());
    }

    private ProductTranslationInput getProductTranslationInput() {
        return ProductTranslationInput.builder()
                .name(TOMATO_NAME)
                .language(Language.ENGLISH)
                .build();
    }

    private Product getOriginalProduct() {
        return Product.builder()
                .id(PRODUCT_ID)
                .name("Помидор")
                .langGroupId(LANG_GROUP_ID)
                .language(null)
                .productType(ProductType.VEGETABLE)
                .consistence(Consistence.SOLID)
                .build();

    }
}
