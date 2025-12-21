package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.domain.dto.input.ConversionUnitsInput;
import com.mydiet.mydiet.domain.entity.DailyDiet;
import com.mydiet.mydiet.infrastructure.ConversionUnits;
import com.mydiet.mydiet.infrastructure.ConversionUnitsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversion-units")
@Api(tags = "Conversion Units")
@RequiredArgsConstructor
public class ConversionController {

    private final ConversionUnitsService conversionUnitsService;

    @GetMapping("/{productId}/all-applicable-quantity-units")
    @ApiOperation(value = "Get all Quantity Units applicable to the Product", notes = "Info endpoint")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Quantity units received", response = DailyDiet.class),
        @ApiResponse(code = 404, message = "Product does not exist", response = ErrorMessage.class)})
    public ResponseEntity<List<String>> getAllUnitsApplicableForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(conversionUnitsService.getAllAvailableUnitsForProduct(productId));
    }

    @GetMapping("/{productId}/available-for-setting-coefficients")
    @ApiOperation(
            value = "Returns a list with Quantity Units' names for which Conversion Coefficients are available to set",
            notes = "Info endpoint"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Quantity units received", response = DailyDiet.class),
            @ApiResponse(code = 404, message = "Product does not exist", response = ErrorMessage.class)})
    public ResponseEntity<List<String>> getUnitsWhichCoefficientsAreAvailableToBeSetFor(@PathVariable Long productId) {
        return ResponseEntity.ok(conversionUnitsService.getConvertibleUnitsForProduct(productId));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ConversionUnits> updateConversionCoefficientsForProduct(
            @PathVariable Long productId,
            @RequestBody ConversionUnitsInput convUnitsUpdate
    ) {
        var convUnits = conversionUnitsService.updateConvCoefficientsForProduct(productId, convUnitsUpdate);
        return ResponseEntity.ok(convUnits);
    }

    @GetMapping("/readiness-for-program/{programNumber}")
    public ResponseEntity<Void> checkConversionUnitsReadiness(@PathVariable Long programNumber) {

        // todo

        return null;
    }

}
