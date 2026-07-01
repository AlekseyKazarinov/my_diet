package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.config.SwaggerConfig;
import com.mydiet.mydiet.domain.dto.input.ProductExclusion;
import com.mydiet.mydiet.domain.dto.output.android.NutritionProgramAppContainer;
import com.mydiet.mydiet.domain.dto.output.android.NutritionProgramPreview;
import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.infrastructure.ShoppingListService;
import com.mydiet.mydiet.repository.NutritionProgramRepository;
import com.mydiet.mydiet.service.NutritionProgramConverterService;
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

import java.util.List;
import java.util.Set;

import static com.mydiet.mydiet.domain.entity.Status.PUBLISHED;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Tag(name = SwaggerConfig.APP_CONTROLLER_TAG) // Заменено с @Api
public class AppController {

    private final NutritionProgramRepository       nutritionProgramRepository;
    private final NutritionProgramConverterService nutritionProgramConverterService;
    private final ShoppingListService              shoppingListService;

    @GetMapping(path = "/nutrition-programs/{programNumber}")
    @Operation(summary = "Get a Nutrition Program by Id") // Заменено с @ApiOperation
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nutrition Program received", content = @Content(schema = @Schema(implementation = NutritionProgramAppContainer.class))),
            @ApiResponse(responseCode = "404", description = "Nutrition Program not found", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Nutrition Program has not been published and App user does not have access", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<NutritionProgramAppContainer> getNutritionProgram(@PathVariable Long programNumber) {
        var programApp = nutritionProgramConverterService.getProgramConvertedIntoAppOutputFormat(programNumber);

        return ResponseEntity.ok(programApp);
    }

    @GetMapping(path = "/nutrition-programs/{programName}")
    @Operation(summary = "Get a Nutrition Program by Name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nutrition Program received", content = @Content(schema = @Schema(implementation = NutritionProgramAppContainer.class))),
            @ApiResponse(responseCode = "204", description = "Nutrition Program does not exist")
    })
    public ResponseEntity<NutritionProgramAppContainer> getNutritionProgramByName(@PathVariable String programName) {
        var programApp = nutritionProgramConverterService.getProgramConvertedIntoAppOutputFormat(programName);

        return ResponseEntity.ok(programApp);
    }

    // todo: use projections-based approach. See point 5: https://www.baeldung.com/spring-data-jpa-projections
    @GetMapping(path = "/nutrition-programs/previews")
    @Operation(summary = "Get Nutrition Program previews", description = "All parameters are optional. You can combine them in any way")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nutrition Program previews received", content = @Content(schema = @Schema(implementation = NutritionProgramPreview.class))),
            @ApiResponse(responseCode = "204", description = "Nutrition Programs not found", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<List<NutritionProgramPreview>> getNutritionProgram(
            @RequestParam(defaultValue = "RUSSIAN") Language language,
            @RequestParam(required = false) Integer kcal,
            @RequestParam(required = false) Integer delta,
            @RequestParam(required = false) Set<Lifestyle> lifestyles,
            @RequestParam(defaultValue = "5", required = false) Integer maxNumber,
            @RequestBody(required = false) ProductExclusion productExclusion
    ) {
        var programPreviews = nutritionProgramConverterService.getProgramPreviewsBy(
                language, kcal, delta, lifestyles, productExclusion, maxNumber
        );
        return programPreviews.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(programPreviews);
    }

    @Operation(summary = "Get a Shopping List for Nutrition Program")
    @GetMapping("/shopping-lists/{programNumber}")
    public ResponseEntity<ShoppingList> getShoppingListFor(@PathVariable Long programNumber) {
        var optionalShoppingList = shoppingListService.findShoppingListFor(programNumber);

        return optionalShoppingList.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

}