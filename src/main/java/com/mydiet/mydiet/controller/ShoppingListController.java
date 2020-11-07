package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.entity.ShoppingList;
import com.mydiet.mydiet.domain.entity.WeekList;
import com.mydiet.mydiet.infrastructure.UnitConverterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/shopping-lists")
@RequiredArgsConstructor
@Api(tags = "Shopping Lists")
public class ShoppingListController {

    private final UnitConverterService unitConverterService;

    @ApiOperation(value = "Get a Shopping List for Nutrition Program")
    @GetMapping("/{programNumber}")
    public ResponseEntity<ShoppingList> getShoppingListFor(@PathVariable Long programNumber) {
        var shoppingList = unitConverterService.getShoppingListFor(programNumber);
        return ResponseEntity.ok(shoppingList);
    }

    @ApiOperation(value = "Get a weekly Shopping List for Nutrition Program")
    @GetMapping("/{programNumber}/weeks/{weekNumber}")
    public ResponseEntity<WeekList> getWeekShoppingList(@PathVariable Long programNumber,
                                                        @PathVariable Integer weekNumber
    ) {
        var weekList = unitConverterService.getShoppingListForWeekNo(weekNumber, programNumber);
        return ResponseEntity.ok(weekList);
    }

}
