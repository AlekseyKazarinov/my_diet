package com.mydiet.mydiet.service;

import com.google.common.base.Preconditions;
import com.mydiet.mydiet.domain.dto.input.ProductInput;
import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.ProductType;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.infrastructure.Consistence;
import com.mydiet.mydiet.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductInput productCreationInput) {
        var product = Product.builder()
                .name(productCreationInput.getName())
                .langId(UUID.randomUUID().toString())
                .language(Optional.ofNullable(productCreationInput.getLanguage()).orElse(Language.RUSSIAN))
                .productType(ProductType.of(productCreationInput.getProductType()))
                .consistence(Consistence.of(productCreationInput.getConsistence()))
                .build();

        return saveProduct(product);
    }

    public Product saveProduct(Product product) {
        var optionalStoredProduct = productRepository.findProductByName(product.getName());

        if (optionalStoredProduct.isPresent()) {
            var storedProduct = optionalStoredProduct.get();

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

    public Product updateProductName(Long productId, String newProductName) {
        Utils.validateTextVariableIsSet(newProductName, "product name");
        var product = getProductOrThrow(productId);
        product.setName(newProductName);
        return saveProduct(product);
    }

    public Product updateProduct(Long productId, ProductInput productUpdateInput) {
        var product = getProductOrThrow(productId);

        product.setName(productUpdateInput.getName());
        product.setProductType(ProductType.of(productUpdateInput.getProductType()));
        product.setLanguage(Optional.ofNullable(productUpdateInput.getLanguage()).orElse(Language.RUSSIAN));

        return saveProduct(product);
    }

    public Product createValidatedProduct(ProductInput input) {
        validateProductInput(input);
        return createProduct(input);
    }

    public void validateProductInput(ProductInput input) {
        Preconditions.checkNotNull(input, "Product is null");

        Utils.validateStringFieldIsSet(input.getName(), "Name", input);
        ProductType.validateDescription(input.getProductType());
        Consistence.validateConsistence(input.getConsistence());
    }

}
