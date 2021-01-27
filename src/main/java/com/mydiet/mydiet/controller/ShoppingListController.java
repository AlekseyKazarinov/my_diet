package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.entity.ShoppingList;
import com.mydiet.mydiet.domain.entity.WeekList;
import com.mydiet.mydiet.infrastructure.ShoppingListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/shopping-lists")
@RequiredArgsConstructor
@Api(tags = "Shopping Lists")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    @ApiOperation(value = "Get a Shopping List for Nutrition Program")
    @GetMapping("/{programNumber}")
    public ResponseEntity<ShoppingList> getShoppingListFor(@PathVariable Long programNumber) {
        var optionalShoppingList = shoppingListService.getShoppingListFor(programNumber);

        return optionalShoppingList.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @ApiOperation(value = "Get a weekly Shopping List for Nutrition Program")
    @GetMapping("/{programNumber}/weeks/{weekNumber}")
    public ResponseEntity<WeekList> getWeekShoppingList(@PathVariable Long programNumber,
                                                        @PathVariable Integer weekNumber
    ) {
        var weekList = shoppingListService.getShoppingListForWeekNo(weekNumber, programNumber);
        return ResponseEntity.ok(weekList);
    }

    @ApiOperation(value = "Update Shopping List for an Nutrition Program")
    @PutMapping("/{programNumber}/weeks/{weekNumber}")
    public ResponseEntity<ShoppingList> updateShoppingList(
            @PathVariable Long programNumber,
            @PathVariable Integer weekNumber,
            @RequestBody WeekList weekList
    ) {
        var shoppingList = shoppingListService.replaceWeekInShoppingListFor(programNumber, weekNumber, weekList);
        return ResponseEntity.ok(shoppingList);
    }

    @ApiOperation(value = "Update Shopping List for an Nutrition Program")
    @PutMapping("/{programNumber}")
    public ResponseEntity<String> updateShoppingList(
            @PathVariable Long programNumber,
            @RequestBody ShoppingList shoppingList
    ) {
        shoppingListService.replaceShoppingListFor(programNumber, shoppingList);
        return ResponseEntity.ok("Shopping List has been successfully updated for Nutrition Program #" + programNumber);
    }

}
