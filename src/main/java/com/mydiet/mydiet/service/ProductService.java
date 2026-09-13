package com.mydiet.mydiet.service;

import com.google.common.base.Preconditions;
import com.mydiet.mydiet.domain.dto.input.ProductInput;
import com.mydiet.mydiet.domain.dto.input.ProductTranslationInput;
import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.mydiet.mydiet.domain.entity.Language.RUSSIAN;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createValidatedProduct(ProductInput input) {
        validateProductInput(input);
        return createProduct(input);
    }

    public Product updateValidatedProduct(Long productId, ProductInput input) {
        validateProductInput(input);
        return updateProduct(productId, input);
    }

    public Product createProduct(ProductInput productCreationInput) {
        var product = Product.builder()
                .name(productCreationInput.getName())
                .langGroupId(Optional.ofNullable(productCreationInput.getLangGroupId()).orElse(UUID.randomUUID().toString()))
                .language(Optional.ofNullable(productCreationInput.getLanguage()).orElse(RUSSIAN))
                .productType(productCreationInput.getProductType())
                .consistence(productCreationInput.getConsistence())
                .build();

        return saveProduct(product);
    }

    public Product saveProduct(Product product) {
        var optionalStoredSameProduct = productRepository.findProductByName(product.getName());

        if (optionalStoredSameProduct.isPresent()) {
            var storedProduct = optionalStoredSameProduct.get();

            if (!storedProduct.getProductType().equals(product.getProductType())) {
                var message = String.format(
                        "Failed to store Product. Here is already stored Product with Name: '%s' and ProductType: '%s'",
                            storedProduct.getName(), storedProduct.getProductType());

                throw new ValidationException(message);
            }
            log.info("do not save Product");
            return storedProduct;
        }
        return productRepository.save(product);
    }

    public Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Product with id: %s does not exist", productId))
                );
    }

    public Product getProductByLangGroupIdOrThrow(String landGroupId, Language language) {
        return productRepository.findProductByLangGroupIdAndLanguage(landGroupId, language)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format(
                                        "Product with langGroupId: %s and language: %s does not exist",
                                        landGroupId, language))
                );
    }

    public Product updateProductName(Long productId, String newProductName) {
        Utils.validateTextVariableIsSet(newProductName, "product name");
        var product = getProductOrThrow(productId);
        product.setName(newProductName);
        return saveProduct(product);
    }

    public Product updateProduct(Long productId, ProductInput productUpdateInput) {
        var product = getProductOrThrow(productId);

        product.setName(productUpdateInput.getName());
        product.setProductType(productUpdateInput.getProductType());
        product.setLanguage(Optional.ofNullable(productUpdateInput.getLanguage()).orElse(RUSSIAN));

        return saveProduct(product);
    }

    public Product translateProduct(Long productId, ProductTranslationInput productTranslationInput) {
        var originalProduct = getProductOrThrow(productId);

        if (Language.areEqual(originalProduct.getLanguage(), productTranslationInput.getLanguage())) {
            throw new ValidationException("Product can not be translated into the same language. It is already in needed language.");
        }

        var langGroupId = Optional.ofNullable(originalProduct.getLangGroupId()).orElse(UUID.randomUUID().toString());

        if (originalProduct.getLangGroupId() == null) {
            originalProduct.setLangGroupId(langGroupId);
            productRepository.save(originalProduct);
        }

        var optionalAlreadyTranslatedProduct = findProductTranslationInto(
                productTranslationInput.getLanguage(),
                originalProduct
        );

        if (optionalAlreadyTranslatedProduct.isPresent()) {
            throw new ValidationException(
                    String.format("No needed translation. Translation into %s for Recipe with Id #%s is Recipe #%s",
                            Language.isRussian(productTranslationInput.getLanguage()) ? RUSSIAN : productTranslationInput.getLanguage(),
                            originalProduct.getId(),
                            optionalAlreadyTranslatedProduct.get().getId()
                    ));
        }

        var productInput = convertToProductInput(originalProduct, productTranslationInput);
        var translatedProduct = createProduct(productInput);

        return productRepository.save(translatedProduct);

    }

    public void validateProductInput(ProductInput input) {
        Preconditions.checkNotNull(input, "Product is null");

        Utils.validateTextFieldIsSet(input.getName(), "Name", input);
        //ProductType.validateDescription(input.getProductType());
        //Consistence.validateConsistence(input.getConsistence());
    }

    public Optional<Product> findProductTranslationInto(Language language, Product product) {
        return productRepository.findProductByLangGroupIdAndLanguage(
                product.getLangGroupId(), language
        );
    }

    public ProductInput convertToProductInput(Product originalProduct, ProductTranslationInput productTranslationInput) {
        return ProductInput.builder()
                .language(productTranslationInput.getLanguage())
                .langGroupId(Optional.ofNullable(originalProduct.getLangGroupId())
                        .orElse(UUID.randomUUID().toString())
                )

                .name(productTranslationInput.getName())

                .productType(originalProduct.getProductType())
                .consistence(originalProduct.getConsistence())
                .build();
    }

}
