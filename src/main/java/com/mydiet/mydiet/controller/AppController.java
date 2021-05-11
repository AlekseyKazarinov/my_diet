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
import io.swagger.annotations.*;
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
@Api(tags = SwaggerConfig.APP_CONTROLLER_TAG)
public class AppController {

    private final NutritionProgramRepository       nutritionProgramRepository;
    private final NutritionProgramConverterService nutritionProgramConverterService;
    private final ShoppingListService              shoppingListService;

    @GetMapping(path = "/nutrition-programs/{programNumber}")
    @ApiOperation(value = "Get a Nutrition Program by Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Nutrition Program received", response = NutritionProgramAppContainer.class),
            @ApiResponse(code = 404, message = "Nutrition Program not found", response = ErrorMessage.class),
            @ApiResponse(code = 403, message = "Nutrition Program has not been published and App user does not have access", response = ErrorMessage.class)
    })
    public ResponseEntity<NutritionProgramAppContainer> getNutritionProgram(@PathVariable Long programNumber) {
        var programApp = nutritionProgramConverterService.getProgramConvertedIntoAppOutputFormat(programNumber);

        return ResponseEntity.ok(programApp);
    }

    @GetMapping(path = "/nutrition-programs/{programName}")
    @ApiOperation(value = "Get a Nutrition Program by Name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Nutrition Program received", response = NutritionProgramAppContainer.class),
            @ApiResponse(code = 204, message = "Nutrition Program does not exist")
    })
    public ResponseEntity<NutritionProgramAppContainer> getNutritionProgramByName(@PathVariable String programName) {
        var programApp = nutritionProgramConverterService.getProgramConvertedIntoAppOutputFormat(programName);

        return ResponseEntity.ok(programApp);
    }

    @GetMapping(path = "/nutrition-programs/previews")
    @ApiOperation(value = "Get Nutrition Program previews", notes = "All parameters are optional. You can combine them in any way")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Nutrition Program previews received", response = NutritionProgramPreview[].class),
            @ApiResponse(code = 204, message = "Nutrition Programs not found", response = ErrorMessage.class)
    })
    public ResponseEntity<List<NutritionProgramPreview>> getNutritionProgram(
            @RequestParam(defaultValue = "RUSSIAN") Language language,
            @RequestParam(required = false) Integer kcal,
            @RequestParam(required = false) Integer delta,
            @RequestParam(required = false) Set<Lifestyle> lifestyles,
            @RequestParam(required = false) Integer maxNumber,
            @RequestBody(required = false) ProductExclusion productExclusion
    ) {
        var programPreviews = nutritionProgramConverterService.getProgramPreviewsBy(
                language, kcal, delta, lifestyles, productExclusion, maxNumber
        );
        return programPreviews.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(programPreviews);
    }

    @ApiOperation(value = "Get a Shopping List for Nutrition Program")
    @GetMapping("/shopping-lists/{programNumber}")
    public ResponseEntity<ShoppingList> getShoppingListFor(@PathVariable Long programNumber) {
        var optionalShoppingList = shoppingListService.findShoppingListFor(programNumber);

        return optionalShoppingList.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

}
