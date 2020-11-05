package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.infrastructure.ConversionUnits;
import com.mydiet.mydiet.infrastructure.ConversionUnitsService;
import com.mydiet.mydiet.service.ProductService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
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
    private final ConversionUnitsService conversionUnitsService;

    @GetMapping
    public List<String> getProducts() {
        var productsList = new ArrayList<String>();
        productsList.add("Honey");
        productsList.add("Almond");
        return productsList;
    }

    @PostMapping
    public String createProduct() {
        return "Product is saved successfully";
    }

    @GetMapping("/{productId}/conversion-units/available-to-set")
    public ResponseEntity<List<String>> getUnitsWhichCoefficientsAreAvailableToBeSetFor(@PathVariable Long productId) {
        return ResponseEntity.ok(conversionUnitsService.getConvertibleUnitsForProduct(productId));
    }

    @PatchMapping("/{productId}/conversion-units")
    public ResponseEntity<ConversionUnits> updateConversionCoefficientsForProduct(
            @PathVariable Long productId,
            @RequestBody ConversionUnits convUnitsUpdate
    ) {
        var convUnits = conversionUnitsService.updateConvCoefficientsForProduct(productId, convUnitsUpdate);
        return ResponseEntity.ok(convUnits);
    }


}
