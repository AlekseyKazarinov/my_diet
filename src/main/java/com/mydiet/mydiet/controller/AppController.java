package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.config.SwaggerConfig;
import com.mydiet.mydiet.domain.dto.output.android.NutritionProgramAppContainer;
import com.mydiet.mydiet.domain.entity.NutritionProgram;
import com.mydiet.mydiet.repository.NutritionProgramRepository;
import com.mydiet.mydiet.service.NutritionProgramConverterService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.mydiet.mydiet.domain.entity.Status.PUBLISHED;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Api(tags = SwaggerConfig.APP_CONTROLLER_TAG)
public class AppController {

    private final NutritionProgramRepository       nutritionProgramRepository;
    private final NutritionProgramConverterService nutritionProgramConverterService;

    @GetMapping(path = "/nutrition-programs/{programNumber}")
    @ApiOperation(value = "Get a Nutrition Program")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Nutrition Program received", response = NutritionProgramAppContainer.class),
            @ApiResponse(code = 404, message = "Nutrition Program not found", response = ErrorMessage.class),
            @ApiResponse(code = 403, message = "Nutrition Program has not been published and App user does not have access", response = ErrorMessage.class)
    })
    public ResponseEntity<NutritionProgramAppContainer> getNutritionProgram(@PathVariable Long programNumber) {
        var programApp = nutritionProgramConverterService.getProgramConvertedIntoAppOutputFormat(programNumber);

        return ResponseEntity.ok(programApp);
    }

    // temporary unused. Delete?
    @GetMapping(path = "/nutrition-programs/{programName}")
    @ApiOperation(value = "Get a Nutrition Program")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Nutrition Program received", response = NutritionProgram.class),
            @ApiResponse(code = 204, message = "Nutrition Program does not exist")
    })
    public ResponseEntity<NutritionProgram> getNutritionProgramByName(@PathVariable String programName) {
        var optionalProgram = nutritionProgramRepository.findProgramByNameAndStatus(programName, PUBLISHED);

        return optionalProgram.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    // todo: General endpoint for retrieving all published programs

    // todo: Nutrition Programs with short description

    // todo: retrieve shopping list in app compatible format

}
