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

@RestController
@RequestMapping(path = "/nutrition-programs")
@RequiredArgsConstructor
@Tag(name = "Nutrition Programs") // Заменено с @Api(tags = ...)
public class NutritionProgramController {

    private final NutritionProgramService nutritionProgramService;

    @PostMapping
    @Operation(summary = "Create a new Nutrition Program")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nutrition Program created", content = @Content(schema = @Schema(implementation = NutritionProgram.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<NutritionProgram> createNutritionProgram(
            @RequestBody NutritionProgramInput nutritionProgramInput
    ) {
        var program = nutritionProgramService.createValidatedNutritionProgram(nutritionProgramInput);

        return ResponseEntity.status(HttpStatus.CREATED).body(program);
    }

    @PostMapping("/{programNumber}/translate")
    @Operation(summary = "Translate existing Nutrition Program")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nutrition Program translated", content = @Content(schema = @Schema(implementation = NutritionProgram.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
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
    @Operation(summary = "Get a Nutrition Program")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nutrition Program received", content = @Content(schema = @Schema(implementation = NutritionProgram.class))),
            @ApiResponse(responseCode = "204", description = "Nutrition Program does not exist")
    })
    public ResponseEntity<NutritionProgram> getNutritionProgram(@PathVariable Long programNumber) {
        var optionalProgram = nutritionProgramService.findNutritionProgram(programNumber);

        return optionalProgram.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PatchMapping(path = "/{programNumber}/update")
    @Operation(summary = "Update fields on Nutrition Program layer")
    public ResponseEntity<NutritionProgram> updateNutritionProgram(
            @PathVariable Long programNumber,
            @RequestBody BaseNutritionProgramInput baseNutritionProgramInput
    ) {
        var updatedProgram = nutritionProgramService.updateNutritionProgram(programNumber, baseNutritionProgramInput);
        return ResponseEntity.accepted().body(updatedProgram);
    }

    @GetMapping(path = "/count")
    @Operation(summary = "Get total number of programs")
    // Примечание: в оригинале здесь был указан response = NutritionProgram.class, хотя метод возвращает Long.
    // Я оставил NutritionProgram.class для строгого соответствия, но скорее всего тут должно быть Long.class.
    @ApiResponse(responseCode = "200", description = "Total number calculated", content = @Content(schema = @Schema(implementation = NutritionProgram.class)))
    public ResponseEntity<Long> countPrograms(@RequestParam(required = false) Language language) {
        var numberOfPrograms = language == null ?
                nutritionProgramService.getTotalNumberOfAllPrograms() :
                nutritionProgramService.getTotalNumberOfProgramsWithLanguage(language);
        return ResponseEntity.ok(numberOfPrograms);
    }

    /**
     * General endpoint for retrieving all programs
     */
    @GetMapping
    @Operation(
            summary = "General endpoint for retrieving all programs which covers all needed cases",
            description = "Very useful. All parameters are optional. You can combine them in any way" // Убрал лишний экранирующий слэш из оригинала
    )
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
                language, kcal, delta, status, lifestyles, productExclusion, maxNumber
        );
        return programs.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(programs);
    }

    @PutMapping(path = "/{programNumber}/accept")
    @Operation(summary = "Set Nutrition Program Status to 'Accepted'")
    @ApiResponse(responseCode = "202", description = "Accepted", content = @Content(schema = @Schema(implementation = NutritionProgram.class)))
    public ResponseEntity<NutritionProgram> acceptProgram(@PathVariable Long programNumber) {
        var program =  nutritionProgramService.acceptProgram(programNumber);
        return ResponseEntity.ok(program);
    }

    @PutMapping(path = "/{programNumber}/publish")
    @Operation(summary = "Set Nutrition Program Status to 'Published'")
    @ApiResponse(responseCode = "202", description = "Published", content = @Content(schema = @Schema(implementation = NutritionProgram.class)))
    public ResponseEntity<NutritionProgram> publishProgram(@PathVariable Long programNumber) {
        var program =  nutritionProgramService.publishProgram(programNumber);
        return ResponseEntity.ok(program);
    }

    @PutMapping(path = "/{programNumber}/revert")
    @Operation(summary = "Revert Nutrition Program Status")
    @ApiResponse(responseCode = "202", description = "Reverted", content = @Content(schema = @Schema(implementation = NutritionProgram.class)))
    public ResponseEntity<NutritionProgram> revertProgram(@PathVariable Long programNumber) {
        var program =  nutritionProgramService.revertProgram(programNumber);
        return ResponseEntity.ok(program);
    }

    // todo: it does not work. Need a fix!
    @PostMapping(path = "/{programNumber}/daily-diet/{dailyDietId}")
    @Operation(summary = "Add an existing Daily Diet to Nutrition Program")
    @ApiResponse(responseCode = "200", description = "Daily Diet was successfully added", content = @Content(schema = @Schema(implementation = NutritionProgram.class)))
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
    @Operation(summary = "Delete Nutrition Program by Id")
    public ResponseEntity<Void> deleteNutritionProgram(@PathVariable Long programNumber) {
        nutritionProgramService.deleteProgram(programNumber);
        return ResponseEntity.noContent().build();
    }

}