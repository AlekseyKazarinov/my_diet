package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.domain.dto.input.NutritionProgramInput;
import com.mydiet.mydiet.domain.dto.input.ProductExclusion;
import com.mydiet.mydiet.domain.entity.Lifestyle;
import com.mydiet.mydiet.domain.entity.NutritionProgram;
import com.mydiet.mydiet.domain.entity.Status;
import com.mydiet.mydiet.service.NutritionProgramService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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

    // todo: получить программы в состоянии DRAFT, ACCEPTED, PUBLISHED

    @GetMapping("/")
    public ResponseEntity<List<NutritionProgram>> getNutritionPrograms(
             @RequestParam Integer kcal,
             @RequestParam Status status,
             @RequestParam(required = false) Set<Lifestyle> lifestyles,
             @RequestBody(required = false) ProductExclusion productExclusion
    ) {
        var programs = nutritionProgramService.getProgramsBy(kcal, status, lifestyles, productExclusion);
        return ResponseEntity.ok(programs);
    }

    @PutMapping(path = "/{programNumber}/accept")
    @ApiOperation(value = "Set Nutrition Program Status to 'Accepted'")
    @ApiResponse(code = 202, message = "Accepted", response = NutritionProgram.class)
    public ResponseEntity<NutritionProgram> acceptProgram(@PathVariable Long programNumber) {
        var program =  nutritionProgramService.acceptProgram(programNumber);
        return ResponseEntity.ok(program);
    }

    @PutMapping(path = "/{programNumber}/publish")
    @ApiOperation(value = "Set Nutrition Program Status to 'Published'")
    @ApiResponse(code = 202, message = "Published", response = NutritionProgram.class)
    public ResponseEntity<NutritionProgram> publishProgram(@PathVariable Long programNumber) {
        var program =  nutritionProgramService.publishProgram(programNumber);
        return ResponseEntity.ok(program);
    }

    @PutMapping(path = "/{programNumber}/revert")
    @ApiOperation(value = "Revert Nutrition Program Status")
    @ApiResponse(code = 202, message = "Reverted", response = NutritionProgram.class)
    public ResponseEntity<NutritionProgram> revertProgram(@PathVariable Long programNumber) {
        var program =  nutritionProgramService.revertProgram(programNumber);
        return ResponseEntity.ok(program);
    }

    // todo: it does not work. Need a fix!
    @PostMapping(path = "/{programNumber}/daily-diet/{dailyDietId}")
    @ApiOperation(value = "Add an existing Daily Diet to Nutrition Program")
    @ApiResponse(code = 200, message = "Daily Diet was successfully added", response = NutritionProgram.class)
    public ResponseEntity<NutritionProgram> addDailyDietToProgram(
            @PathVariable Long programNumber,
            @PathVariable Long dailyDietId
    ) {
        // todo: implement the functionality. Ensure program has DRAFT status
        return ResponseEntity.ok(NutritionProgram.builder().build());
    }

    // update for admin only (ROLE CHECK)
    // get for users  (without authorization)

    // todo: download N programs, list of numbers for programs not to send
    //



}
