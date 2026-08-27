package com.mydiet.mydiet.controller;

import com.mydiet.mydiet.domain.entity.ShoppingList;
import com.mydiet.mydiet.domain.entity.WeekList;
import com.mydiet.mydiet.service.ShoppingListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/shopping-lists")
@RequiredArgsConstructor
@Tag(name = "Shopping Lists") // Заменено с @Api(tags = ...)
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    @Operation(summary = "Get a Shopping List for Nutrition Program") // Заменено с @ApiOperation
    @GetMapping("/{programNumber}")
    public ResponseEntity<ShoppingList> getShoppingListFor(@PathVariable Long programNumber) {
        var optionalShoppingList = shoppingListService.findShoppingListFor(programNumber);

        return optionalShoppingList.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @Operation(summary = "Get a weekly Shopping List for Nutrition Program")
    @GetMapping("/{programNumber}/weeks/{weekNumber}")
    public ResponseEntity<WeekList> getWeekShoppingList(@PathVariable Long programNumber,
                                                        @PathVariable Integer weekNumber
    ) {
        var weekList = shoppingListService.getShoppingListForWeekNo(weekNumber, programNumber);
        return Optional.ofNullable(weekList)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Update Shopping List for an Nutrition Program")
    @PutMapping("/{programNumber}/weeks/{weekNumber}")
    public ResponseEntity<ShoppingList> updateShoppingList(
            @PathVariable Long programNumber,
            @PathVariable Integer weekNumber,
            @RequestBody WeekList weekList
    ) {
        var shoppingList = shoppingListService.replaceWeekInShoppingListFor(programNumber, weekNumber, weekList);
        return ResponseEntity.ok(shoppingList);
    }

    @Operation(summary = "Update Shopping List for an Nutrition Program")
    @PutMapping("/{programNumber}")
    public ResponseEntity<String> updateShoppingList(
            @PathVariable Long programNumber,
            @RequestBody ShoppingList shoppingList
    ) {
        shoppingListService.replaceShoppingListFor(programNumber, shoppingList);
        return ResponseEntity.ok("Shopping List has been successfully updated for Nutrition Program #" + programNumber);
    }

}