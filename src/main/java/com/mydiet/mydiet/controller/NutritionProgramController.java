package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.domain.dto.NutritionProgramInput;
import com.mydiet.mydiet.domain.entity.NutritionProgram;
import com.mydiet.mydiet.service.NutritionProgramService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/nutrition-programs")
@RequiredArgsConstructor
@Api(tags = "Nutrition Programs")
public class NutritionProgramController {

    private final NutritionProgramService nutritionProgramService;

    @PostMapping
    @ApiOperation(value = "Create a new Nutrition Program")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Nutrition Program created", response = NutritionProgram.class),
            @ApiResponse(code = 400, message = "Validation error", response = ErrorMessage.class)
    })
    public ResponseEntity<NutritionProgram> createNutritionProgram(
            @RequestBody NutritionProgramInput nutritionProgramInput
    ) {
        var program = nutritionProgramService.createValidatedNutritionProgram(nutritionProgramInput);

        return ResponseEntity.status(HttpStatus.CREATED).body(program);
    }

    @GetMapping(path = "/{programNumber}")
    @ApiOperation(value = "Get a Nutrition Program")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Nutrition Program received", response = NutritionProgram.class),
            @ApiResponse(code = 204, message = "Nutrition Program does not exist")
    })
    public ResponseEntity<NutritionProgram> getNutritionProgram(@PathVariable Long programNumber) {
        var optionalProgram = nutritionProgramService.findNutritionProgram(programNumber);

        return optionalProgram.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @GetMapping(path = "/count")
    @ApiOperation(value = "Get total number of programs")
    @ApiResponse(code = 200, message = "Total number calculated", response = NutritionProgram.class)
    public ResponseEntity<Long> countPrograms() {
        var numberOfPrograms = nutritionProgramService.getTotalNumberOfPrograms();
        return ResponseEntity.ok(numberOfPrograms);
    }

    @PostMapping(path = "/{programNumber}/daily-diet/{dailyDietId}")
    @ApiOperation(value = "Add an existing Daily Diet to Nutrition Program")
    @ApiResponse(code = 200, message = "Daily Diet was successfully added", response = NutritionProgram.class)
    public ResponseEntity<NutritionProgram> addDailyDietToProgram(
            @PathVariable Long programNumber,
            @PathVariable Long dailyDietId
    ) {
        // todo: implement the functionality
        return ResponseEntity.ok(NutritionProgram.builder().build());
    }

    // update for admin only (ROLE CHECK)
    // get for users  (without authorization)

    // todo: download N programs, list of numbers for programs not to send
    //



}
