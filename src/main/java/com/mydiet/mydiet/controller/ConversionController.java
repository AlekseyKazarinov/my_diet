package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.domain.dto.input.ConversionUnitsInput;
import com.mydiet.mydiet.domain.entity.DailyDiet;
import com.mydiet.mydiet.infrastructure.ConversionUnits;
import com.mydiet.mydiet.infrastructure.ConversionUnitsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversion-units")
@Tag(name = "Conversion Units") // Заменено с @Api(tags = ...)
@RequiredArgsConstructor
public class ConversionController {

    private final ConversionUnitsService conversionUnitsService;

    @GetMapping("/{productId}/all-applicable-quantity-units")
    @Operation(summary = "Get all Quantity Units applicable to the Product", description = "Info endpoint") // Заменено с @ApiOperation
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity units received", content = @Content(schema = @Schema(implementation = DailyDiet.class))),
            @ApiResponse(responseCode = "404", description = "Product does not exist", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<List<String>> getAllUnitsApplicableForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(conversionUnitsService.getAllAvailableUnitsForProduct(productId));
    }

    @GetMapping("/{productId}/available-for-setting-coefficients")
    @Operation(
            summary = "Returns a list with Quantity Units' names for which Conversion Coefficients are available to set",
            description = "Info endpoint"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity units received", content = @Content(schema = @Schema(implementation = DailyDiet.class))),
            @ApiResponse(responseCode = "404", description = "Product does not exist", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
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