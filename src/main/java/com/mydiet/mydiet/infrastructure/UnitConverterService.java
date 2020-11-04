package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.service.NutritionProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UnitConverterService {

    private final NutritionProgramService programService;
    private final UnitGraphService unitGraphService;

    /**
     * Generates all weekly shopping lists for nutrition program
     * @param nutritionProgram - program for which the list to be created
     * @return
     */
   public ShoppingList generateShoppingListFor(NutritionProgram nutritionProgram) {
       var numberOfWeeks = programService.getNumberOfWeeksFor(nutritionProgram);

       var shoppingList = new ShoppingList();
       shoppingList.setNutritionProgramNumber(nutritionProgram.getNumber());
       shoppingList.setListsByWeek(new ArrayList<>());

       for (int n = 1; n <= numberOfWeeks; n++) {
           var dailyDiets = programService.getDailyDietsForWeekNo(n, nutritionProgram);
           var weekList = new WeekList();
           weekList.setNumberOfWeek(n);

           var ingredientsForWeek = new ArrayList<Ingredient>();
           dailyDiets.forEach(dailyDiet -> {
                       dailyDiet.getMeals()
                               .forEach(meal -> ingredientsForWeek.addAll(meal.getRecipe().getIngredients()));
           });
           ingredientsForWeek.sort(Comparator.comparingLong(i -> i.getProduct().getId()));
           
           var productMap = new HashMap<Long, ProductRow>();


           
           for (var ingredient : ingredientsForWeek) {
               addTo(productMap, ingredient.getProduct(), ingredient.getUnit(), ingredient.getTotalQuantity());
           }
           
           shoppingList.getListsByWeek().add(weekList);
       }

       return shoppingList;
   }

    private void addTo(Map<Long, ProductRow> productMap, Product product, QuantityUnit quantityUnit, Double totalQuantity) {
       var productId = product.getId();

        if (!productMap.containsKey(productId)) {
            productMap.put(productId, new ProductRow(product, totalQuantity, quantityUnit));
            return;
        }

        var productRow = productMap.get(productId);

        // ... implement addition to corresponding value in productRow
    }


}
