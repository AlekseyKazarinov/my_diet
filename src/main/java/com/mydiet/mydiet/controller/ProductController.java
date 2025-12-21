package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.dto.input.ProductInput;
import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.repository.ProductRepository;
import com.mydiet.mydiet.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/products")
@Api(tags = "Products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping("/{productId}")
    @ApiOperation("Get Product by Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product received", response = Product.class),
            @ApiResponse(code = 204, message = "Product does not exist", response = Object.class)
    })
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
        return productRepository.findById(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PutMapping("/{productId}")
    @ApiOperation("Update Product by Id")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Product received", response = Product.class),
            @ApiResponse(code = 204, message = "Product does not exist", response = Object.class)
    })
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductInput productUpdateInput
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(productService.updateValidatedProduct(productId, productUpdateInput));
    }

    @PatchMapping("/{productId}/name")
    @ApiOperation("Change Product name by productId")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Product updated", response = Product.class),
            @ApiResponse(code = 404, message = "Product not found", response = Object.class)
    })
    public ResponseEntity<Product> updateProductName(@PathVariable Long productId, @RequestParam String productName) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(productService.updateProductName(productId, productName));
    }

    @DeleteMapping("/{productId}")
    @ApiOperation(value = "This endpoint is not intended for regular using", notes = "API provides such a function just in case. " +
            "Regular using may cause inconsistency between all basic entities making work unstable")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productRepository.deleteById(productId);
        return ResponseEntity.noContent().build();
    }

}
