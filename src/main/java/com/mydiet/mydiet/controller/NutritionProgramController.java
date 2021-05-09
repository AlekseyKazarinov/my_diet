package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.domain.dto.input.BaseNutritionProgramInput;
import com.mydiet.mydiet.domain.dto.input.NutritionProgramInput;
import com.mydiet.mydiet.domain.dto.input.ProductExclusion;
import com.mydiet.mydiet.domain.dto.input.ProgramTranslationInput;
import com.mydiet.mydiet.domain.entity.Language;
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

    @PostMapping("/{programNumber}/translate")
    @ApiOperation(value = "Translate existing Nutrition Program")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Nutrition Program translated", response = NutritionProgram.class),
            @ApiResponse(code = 400, message = "Validation error", response = ErrorMessage.class)
    })
    public ResponseEntity<NutritionProgram> translateNutritionProgram(
            @PathVariable Long programNumber,
            @RequestBody ProgramTranslationInput programTranslationInput
    ) {
        var program = nutritionProgramService.translateValidatedNutritionProgram(
                programNumber, programTranslationInput
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(program);
    }

    @GetMapping(path = "/{programNumber}")
    @ApiOperation(value = "Get a Nutrition Program")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Nutrition Program received", response = NutritionProgram.class),
            @ApiResponse(code = 204, message = "Nutrition Program does not exist", response = Object.class)
    })
    public ResponseEntity<NutritionProgram> getNutritionProgram(@PathVariable Long programNumber) {
        var optionalProgram = nutritionProgramService.findNutritionProgram(programNumber);

        return optionalProgram.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    // todo: update Nutrition Program specific fields

    @PatchMapping(path = "/{programNumber}/update")
    @ApiOperation(value = "Update fields on Nutrition Program layer")
    public ResponseEntity<NutritionProgram> updateNutritionProgram(
            @PathVariable Long programNumber,
            @RequestBody BaseNutritionProgramInput baseNutritionProgramInput
    ) {
        var updatedProgram = nutritionProgramService.updateNutritionProgram(programNumber, baseNutritionProgramInput);
        return ResponseEntity.accepted().body(updatedProgram);
    }

    @GetMapping(path = "/count")
    @ApiOperation(value = "Get total number of programs")
    @ApiResponse(code = 200, message = "Total number calculated", response = NutritionProgram.class)
    public ResponseEntity<Long> countPrograms(@RequestParam(required = false) Language language) {
        var numberOfPrograms = language == null ?
                nutritionProgramService.getTotalNumberOfAllPrograms() :
                nutritionProgramService.getTotalNumberOfProgramsWithLanguage(language);
        return ResponseEntity.ok(numberOfPrograms);
    }

    /**
     * Generic endpoint for retrieving all programs
     */
    @GetMapping("/")
    public ResponseEntity<List<NutritionProgram>> getNutritionPrograms(
            @RequestParam(defaultValue = "RUSSIAN") Language language,
            @RequestParam(required = false) Integer kcal,
            @RequestParam(required = false) Integer delta,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Set<Lifestyle> lifestyles,
            @RequestParam(required = false) Integer maxNumber,
            @RequestBody(required = false) ProductExclusion productExclusion
    ) {
        var programs = nutritionProgramService.getProgramsBy(
                language, kcal, delta, status, lifestyles, productExclusion, maxNumber);
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

    @DeleteMapping(path = "/{programNumber}")
    @ApiOperation(value = "Delete Nutrition Program by Id")
    public ResponseEntity<Void> deleteNutritionProgram(@PathVariable Long programNumber) {
        nutritionProgramService.deleteProgram(programNumber);
        return ResponseEntity.noContent().build();
    }

}
