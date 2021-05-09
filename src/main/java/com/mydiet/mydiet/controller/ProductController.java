package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.dto.input.ConversionUnitsInput;
import com.mydiet.mydiet.domain.dto.input.ProductInput;
import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.infrastructure.ConversionUnits;
import com.mydiet.mydiet.infrastructure.ConversionUnitsService;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/products")
@Api(tags = "Products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ConversionUnitsService conversionUnitsService;

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

    @GetMapping("/{productId}/conversion-units/available-to-set")
    public ResponseEntity<List<String>> getUnitsWhichCoefficientsAreAvailableToBeSetFor(@PathVariable Long productId) {
        return ResponseEntity.ok(conversionUnitsService.getConvertibleUnitsForProduct(productId));
    }

    @PatchMapping("/{productId}/conversion-units")
    public ResponseEntity<ConversionUnits> updateConversionCoefficientsForProduct(
            @PathVariable Long productId,
            @RequestBody ConversionUnitsInput convUnitsUpdate
    ) {
        var convUnits = conversionUnitsService.updateConvCoefficientsForProduct(productId, convUnitsUpdate);
        return ResponseEntity.ok(convUnits);
    }

    // todo: delete Product

}
