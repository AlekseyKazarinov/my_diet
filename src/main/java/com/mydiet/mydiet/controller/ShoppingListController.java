package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.entity.ShoppingList;
import com.mydiet.mydiet.domain.entity.WeekList;
import com.mydiet.mydiet.infrastructure.UnitConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListController {

    private final UnitConverterService unitConverterService;

    @GetMapping("/{programNumber}")
    public ResponseEntity<ShoppingList> getShoppingListFor(@PathVariable Long programNumber) {
        var shoppingList = unitConverterService.getShoppingListFor(programNumber);
        return ResponseEntity.ok(shoppingList);
    }

    @GetMapping("/{programNumber}/weeks/{weekNumber}")
    public ResponseEntity<WeekList> getWeekShoppingList(@PathVariable Long programNumber,
                                                        @PathVariable Integer weekNumber
    ) {
        var weekList = unitConverterService.getShoppingListForWeekNo(weekNumber, programNumber);
        return ResponseEntity.ok(weekList);
    }

}
