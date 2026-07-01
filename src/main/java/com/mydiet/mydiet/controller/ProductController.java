package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.dto.input.ProductInput;
import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.repository.ProductRepository;
import com.mydiet.mydiet.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/products")
@Tag(name = "Products") // Заменено с @Api(tags = ...)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping("/{productId}")
    @Operation(summary = "Get Product by Id") // Заменено с @ApiOperation
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product received", content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "204", description = "Product does not exist")
    })
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
        return productRepository.findById(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update Product by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Product received", content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "204", description = "Product does not exist")
    })
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductInput productUpdateInput
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(productService.updateValidatedProduct(productId, productUpdateInput));
    }

    @PatchMapping("/{productId}/name")
    @Operation(summary = "Change Product name by productId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Product updated", content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found") // Убран response = Object.class, так как для ошибок лучше описывать конкретный класс ошибки (например, ErrorMessage)
    })
    public ResponseEntity<Product> updateProductName(@PathVariable Long productId, @RequestParam String productName) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(productService.updateProductName(productId, productName));
    }

    @DeleteMapping("/{productId}")
    @Operation(
            summary = "This endpoint is not intended for regular using",
            description = "API provides such a function just in case. Regular using may cause inconsistency between all basic entities making work unstable"
    )
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productRepository.deleteById(productId);
        return ResponseEntity.noContent().build();
    }

}