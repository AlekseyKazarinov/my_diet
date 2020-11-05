package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.service.NutritionProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitConverterService {

    private final NutritionProgramService programService;
    private final UnitGraphService unitGraphService;


    public ShoppingList getShoppingListFor(Long programNumber) {
        var nutritionProgram = programService.getProgramOrElseThrow(programNumber);
        return generateShoppingListFor(nutritionProgram);
    }

    public WeekList getShoppingListForWeekNo(Integer weekNumber, Long programNumber) {
        var nutritionProgram = programService.getProgramOrElseThrow(programNumber);
        return generateListOfProductsForWeekNo(weekNumber, nutritionProgram);
    }

    /**
     * Generates all weekly shopping lists for nutrition program
     * @param nutritionProgram a program for which the list to be created
     * @return
     */
   private ShoppingList generateShoppingListFor(NutritionProgram nutritionProgram) {
       var numberOfWeeks = programService.getNumberOfWeeksFor(nutritionProgram);

       var shoppingList = new ShoppingList();
       shoppingList.setNutritionProgramNumber(nutritionProgram.getNumber());
       shoppingList.setListsByWeek(new ArrayList<>());

       for (int n = 1; n <= numberOfWeeks; n++) {
           var weekList = generateListOfProductsForWeekNo(n, nutritionProgram);
           shoppingList.getListsByWeek().add(weekList);
       }

       return shoppingList;
   }

   private WeekList generateListOfProductsForWeekNo(Integer weekNumber, NutritionProgram nutritionProgram) {
       log.info("generate list of products for week number: {}, Nutrition Program: #{}",
               weekNumber, nutritionProgram.getNumber());

       var dailyDiets = programService.getDailyDietsForWeekNo(weekNumber, nutritionProgram);
       var listsByProductType = getProductListsByProductTypeFrom(dailyDiets);

       var weekList = new WeekList();
       weekList.setListsByProductType(listsByProductType);
       weekList.setNumberOfWeek(weekNumber);
       return weekList;
   }

   private Map<ProductType, List<ProductRow>> getProductListsByProductTypeFrom(List<DailyDiet> dailyDiets) {
       var quantityMap = getQuantityListsByProductMapFrom(dailyDiets);
       return convertToProductListsByProductType(quantityMap);
   }

   private Map<Product, List<Quantity>> getQuantityListsByProductMapFrom(List<DailyDiet> dailyDiets) {
       log.info("fetch all Quantities for each Product from dailyDiets");

       var ingredients = new ArrayList<Ingredient>();

       dailyDiets.forEach(dailyDiet -> {
           dailyDiet.getMeals()
                   .forEach(meal -> ingredients.addAll(meal.getRecipe().getIngredients()));
       });

      return ingredients.stream().collect(
              Collectors.groupingBy(
                       Ingredient::getProduct,
                       LinkedHashMap::new,
                       mapping(i -> new Quantity(i.getTotalQuantity(), i.getUnit()), toList())
              ));
   }

   private Map<ProductType, List<ProductRow>> convertToProductListsByProductType(
           Map<Product, List<Quantity>> quantityMap
   ) {
       log.info("convert Quantities for each Product to Product Row for Shopping List");

       var productRowList = new ArrayList<ProductRow>();

       for (var product : quantityMap.keySet()) {
           var quantity = unitGraphService.sum(product, (Quantity[]) quantityMap.get(product).toArray());
           productRowList.add(
                   ProductRow.of(
                           product.getProductType(),
                           product.getName(),
                           quantity.getTotalQuantity(),
                           quantity.getUnit())
           );
       }

       return productRowList.stream().collect(
               Collectors.groupingBy(
                       ProductRow::getProductType,
                       LinkedHashMap::new,
                       Collectors.toList()
               ));
   }

}
