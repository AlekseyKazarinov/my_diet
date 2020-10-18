package com.mydiet.mydiet.service;

import com.google.common.base.Preconditions;
import com.mydiet.mydiet.domain.dto.ProductCreationInput;
import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.ProductType;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductCreationInput input) {
        var product = Product.builder()
                .name(input.getName())
                .productType(ProductType.of(input.getProductType()))
                .build();

        //return product;
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

    public Product createValidatedProduct(ProductCreationInput input) {
        validateProductCreationInput(input);
        return createProduct(input);
    }

    public void validateProductCreationInput(ProductCreationInput input) {
        Preconditions.checkNotNull(input, "Product is null");

        Utils.validateFieldIsSet(input.getName(), input);
        ProductType.validateDescription(input.getProductType());
    }

}
