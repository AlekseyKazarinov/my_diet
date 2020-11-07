package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.config.ErrorMessage;
import com.mydiet.mydiet.domain.dto.input.DailyDietInput;
import com.mydiet.mydiet.domain.entity.DailyDiet;
import com.mydiet.mydiet.service.DailyDietService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily-diets")
@Api(tags = "Daily Diets")
public class DailyDietController {

    private final DailyDietService dailyDietService;

    @PostMapping
    @ApiOperation(value = "Create a new Daily Diet")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Daily Diet created", response = DailyDiet.class),
                           @ApiResponse(code = 400, message = "Validation error", response = ErrorMessage.class)})
    public ResponseEntity<DailyDiet> createDailyDiet(@RequestBody @NonNull DailyDietInput dailyDietCreationInput) {
        var dailyDiet = dailyDietService.createValidatedDailyDiet(dailyDietCreationInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(dailyDiet);
    }

    @GetMapping("/{dailyDietId}")
    @ApiOperation(value = "Get Daily Diet")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Daily Diet received", response = DailyDiet.class),
                           @ApiResponse(code = 204, message = "Daily Diet does not exist")})
    public ResponseEntity<DailyDiet> getDailyDiet(@PathVariable @NonNull Long dailyDietId) {
        var optionalDailyDiet = dailyDietService.findDailyDietById(dailyDietId);
        if (optionalDailyDiet.isPresent()) {
            return ResponseEntity.ok(optionalDailyDiet.get());
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @PatchMapping("/{dailyDietId}/name/{dailyDietName}")
    @ApiOperation(value = "Update name for Daily Diet")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Daily Diet created", response = DailyDiet.class),
            @ApiResponse(code = 400, message = "Validation error", response = ErrorMessage.class)})
    public ResponseEntity<DailyDiet> updateDailyDietName(@PathVariable Long dailyDietId,
                                                         @PathVariable String dailyDietName) {
        var dailyDiet = dailyDietService.updateDailyDietName(dailyDietId, dailyDietName);
        return ResponseEntity.status(HttpStatus.CREATED).body(dailyDiet);
    }

    @DeleteMapping("/{dailyDietId}")
    @ApiOperation(value = "Delete Daily Diet")
    @ApiResponses(value = @ApiResponse(code = 204, message = "Daily Diet deleted"))
    public ResponseEntity<DailyDiet> updateDailyDietName(@PathVariable @NonNull Long dailyDietId) {
        dailyDietService.deleteDailyDiet(dailyDietId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
