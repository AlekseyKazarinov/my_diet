package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.entity.NutritionProgram;
import com.mydiet.mydiet.domain.entity.Status;
import com.mydiet.mydiet.repository.NutritionProgramRepository;
import com.mydiet.mydiet.service.NutritionProgramService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "Android application functionality")
public class AppController {

    private final NutritionProgramRepository nutritionProgramRepository;

    // todo: use app-specific format
    @GetMapping(path = "/nutrition-programs/{programNumber}")
    @ApiOperation(value = "Get a Nutrition Program in application-compatible format")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Nutrition Program received", response = NutritionProgram.class),
            @ApiResponse(code = 204, message = "Nutrition Program does not exist")
    })
    public ResponseEntity<NutritionProgram> getNutritionProgram(@PathVariable Long programNumber) {
        var optionalProgram = nutritionProgramRepository.findProgramByNumberAndStatus(programNumber, PUBLISHED);

        return optionalProgram.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @GetMapping(path = "/nutrition-programs/{programName}")
    @ApiOperation(value = "Get a Nutrition Program in application-compatible format")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Nutrition Program received", response = NutritionProgram.class),
            @ApiResponse(code = 204, message = "Nutrition Program does not exist")
    })
    public ResponseEntity<NutritionProgram> getNutritionProgramByName(@PathVariable String programName) {
        var optionalProgram = nutritionProgramRepository.findProgramByNameAndStatus(programName, PUBLISHED);

        return optionalProgram.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

}
