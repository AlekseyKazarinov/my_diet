package com.mydiet.mydiet.service;

import com.google.common.base.Preconditions;
import com.mydiet.mydiet.domain.dto.ProductCreationInput;
import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.ProductType;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductCreationInput input) {
        validateProductCreationInput(input);

        var product = Product.builder()
                .name(input.getName())
                .productType(ProductType.of(input.getProductType()))
                .build();

        return productRepository.save(product);
    }

    public void validateProductCreationInput(ProductCreationInput input) {
        Preconditions.checkNotNull(input, "Product is null");

        Utils.validateFieldIsSet(input.getName(), input);
        ProductType.validateDescription(input.getProductType());
    }

    public void validateProduct(Product product) {
        Preconditions.checkNotNull(product, "Product is null");
        Utils.validateFieldIsSet(product.getName(), product);

        if (product.getProductType() == null) {
            var message = String.format("ProductType should be set for Product %s", product.getName());
            throw new ValidationException(message);
        }
    }
}
