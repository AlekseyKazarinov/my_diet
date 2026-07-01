package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.domain.dto.input.DailyDietInput;
import com.mydiet.mydiet.domain.entity.DailyDiet;
import com.mydiet.mydiet.service.DailyDietService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily-diets")
@Tag(name = "Daily Diets") // Заменено с @Api(tags = ...)
public class DailyDietController {

    private final DailyDietService dailyDietService;

    @PostMapping
    @Operation(summary = "Create a new Daily Diet") // Заменено с @ApiOperation
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Daily Diet created", content = @Content(schema = @Schema(implementation = DailyDiet.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<DailyDiet> createDailyDiet(@RequestBody @NonNull DailyDietInput dailyDietCreationInput) {
        var dailyDiet = dailyDietService.createValidatedDailyDiet(dailyDietCreationInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(dailyDiet);
    }

    @GetMapping("/{dailyDietId}")
    @Operation(summary = "Get Daily Diet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daily Diet received", content = @Content(schema = @Schema(implementation = DailyDiet.class))),
            @ApiResponse(responseCode = "204", description = "Daily Diet does not exist") // Для 204 No Content тело ответа отсутствует, поэтому schema не нужна
    })
    public ResponseEntity<DailyDiet> getDailyDiet(@PathVariable @NonNull Long dailyDietId) {
        var optionalDailyDiet = dailyDietService.findDailyDietById(dailyDietId);
        return optionalDailyDiet.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PutMapping("/{dailyDietId}/update")
    @Operation(summary = "Update Daily Diet (It is an optional endpoint, you may delete and create DailyDiet again instead)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daily Diet updated", content = @Content(schema = @Schema(implementation = DailyDiet.class))),
            @ApiResponse(responseCode = "404", description = "Daily Diet does not exist", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<DailyDiet> updateDailyDiet(
            @PathVariable Long dailyDietId,
            @RequestBody DailyDietInput dailyDietUpdateInput
    ) {
        var updatedDailyDiet = dailyDietService.updateDailyDiet(dailyDietId, dailyDietUpdateInput);
        return ResponseEntity.accepted().body(updatedDailyDiet);
    }

    @PatchMapping("/{dailyDietId}/name/{dailyDietName}")
    @Operation(summary = "Update name for Daily Diet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Daily Diet updated", content = @Content(schema = @Schema(implementation = DailyDiet.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<DailyDiet> updateDailyDietName(@PathVariable Long dailyDietId,
                                                         @PathVariable String dailyDietName) {
        var dailyDiet = dailyDietService.updateDailyDietName(dailyDietId, dailyDietName);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(dailyDiet);
    }

    @DeleteMapping("/{dailyDietId}")
    @Operation(summary = "Delete Daily Diet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Daily Diet deleted") // Аналогично, для 204 схема не указывается
    })
    public ResponseEntity<DailyDiet> deleteDailyDiet(@PathVariable @NonNull Long dailyDietId) {
        dailyDietService.deleteDailyDiet(dailyDietId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}