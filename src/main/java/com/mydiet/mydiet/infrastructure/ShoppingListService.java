package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.*;
import com.mydiet.mydiet.domain.exception.BadRequestException;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.repository.ShoppingListRepository;
import com.mydiet.mydiet.service.NutritionProgramStorageService;
import com.mydiet.mydiet.service.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.mydiet.mydiet.domain.entity.Status.*;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingListService {

    private final UnitGraphService unitGraphService;
    private final ShoppingListRepository shoppingListRepository;
    private final NutritionProgramStorageService programStorageService;

    public Optional<ShoppingList> findShoppingListFor(Long programNumber) {
        return shoppingListRepository.findById(programNumber);
    }

    public ShoppingList getShoppingListOrElseThrow(Long programNumber) {
        return shoppingListRepository.findById(programNumber)
                .orElseThrow(
                    () -> new NotFoundException("Shopping List was not found for Nutrition Program #" + programNumber)
                );
    }

    public WeekList getShoppingListForWeekNo(Integer weekNumber, Long programNumber) {
        programStorageService.getProgramOrElseThrow(programNumber);
        var shoppingList = getShoppingListOrElseThrow(programNumber);

        return shoppingList.getListForWeekNo(weekNumber);
    }

    public void replaceShoppingListFor(Long programNumber, ShoppingList shoppingList) {
        var program = programStorageService.getProgramOrElseThrow(programNumber);

        throwIfStatusIsIn(program, DRAFT, PUBLISHED);

        shoppingListRepository.deleteById(programNumber);
        shoppingList.setProgram(program);
        shoppingListRepository.save(shoppingList);
    }

    public ShoppingList replaceWeekInShoppingListFor(Long programNumber, Integer weekNumber, WeekList weekList) {
        Utils.validateVariableIsNonNegative(weekNumber, "weekNumber");

        var program = programStorageService.getProgramOrElseThrow(programNumber);
        throwIfStatusIsIn(program, DRAFT, PUBLISHED);

        var shoppingList = getShoppingListOrElseThrow(programNumber);
        var listsByWeek = shoppingList.getListsByWeek();

        if (listsByWeek.size() <= weekNumber) {
            var message = String.format(
                    "Could not replace weekList %s in Shopping List that contains %s weeks",
                    weekNumber,
                    listsByWeek.size()
            );

            throw new BadRequestException(message);
        }

        listsByWeek.set(weekNumber, weekList);
        return shoppingListRepository.save(shoppingList);
    }

    /**
     * Generates all weekly shopping lists for nutrition program
     * @param nutritionProgram a program for which the list to be created
     * @return
     */
   public ShoppingList generateShoppingListFor(NutritionProgram nutritionProgram) {
       shoppingListRepository.deleteById(nutritionProgram.getNumber());
       var numberOfWeeks = nutritionProgram.getNumberOfWeeks();

       var shoppingList = new ShoppingList();
       shoppingList.setNutritionProgramNumber(nutritionProgram.getNumber());
       shoppingList.setListsByWeek(new ArrayList<>());

       for (int n = 1; n <= numberOfWeeks; n++) {
           var weekList = generateListOfProductsForWeekNo(n, nutritionProgram);
           shoppingList.getListsByWeek().add(weekList);
       }

       shoppingList.setProgram(nutritionProgram);

       return shoppingListRepository.save(shoppingList);
   }

   private WeekList generateListOfProductsForWeekNo(Integer weekNumber, NutritionProgram nutritionProgram) {
       log.info("generate list of products for week number: {}, Nutrition Program: #{}",
               weekNumber, nutritionProgram.getNumber());

       var dailyDiets = nutritionProgram.getDailyDietsForWeekNo(weekNumber);
       var listsByProductType = getProductListsByProductTypeFrom(dailyDiets);

       var weekList = new WeekList();
       weekList.setProductListsByType(listsByProductType);
       weekList.setNumberOfWeek(weekNumber);
       return weekList;
   }

   private Map<ProductType, ProductRowsForType> getProductListsByProductTypeFrom(List<DailyDiet> dailyDiets) {
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

   private Map<ProductType, ProductRowsForType> convertToProductListsByProductType(
           Map<Product, List<Quantity>> quantityMap
   ) {
       log.info("convert Quantities for each Product to Product Row for Shopping List: {}", quantityMap);

       var productRowsForTypeMap = new HashMap<ProductType, ProductRowsForType>();

       for (var product : quantityMap.keySet()) {
           var quantity = unitGraphService.sum(product, quantityMap.get(product));

           var value = productRowsForTypeMap.get(product.getProductType());

           if (value == null) {
               value = new ProductRowsForType();
           }

           value.getProductRows().add(
                   ProductRow.builder()
                       .productType(product.getProductType())
                       .productName(product.getName())
                       .totalQuantity(quantity.getTotalQuantity())
                       .unit(quantity.getUnit())
                   .build());

           productRowsForTypeMap.put(product.getProductType(), value);
       }

       return productRowsForTypeMap;
   }

   public void deleteShoppingList(Long programNumber) {
       shoppingListRepository.deleteById(programNumber);
   }

}
