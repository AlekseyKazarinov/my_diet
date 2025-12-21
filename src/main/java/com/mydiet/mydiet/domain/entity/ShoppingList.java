package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mydiet.mydiet.domain.exception.ValidationException;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Data
@NoArgsConstructor
public class ShoppingList {

    @Id
    private Long nutritionProgramNumber;


    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<WeekList> listsByWeek;

    @OneToOne
    @MapsId
    @JsonIgnore
    private NutritionProgram program;

    public Set<String> getAllProductNames() {
        return listsByWeek.stream()
                .flatMap(weekList -> Stream.of(weekList.getProductListsByType()))
                .flatMap(productListsByType -> productListsByType.values().stream())
                .flatMap(productRowsForType -> productRowsForType.getProductRows().stream())
                .map(ProductRow::getProductName)
                .collect(Collectors.toSet());
    }

    public Set<ProductType> getAllProductTypes() {
        return listsByWeek.stream()
                .flatMap(weekList -> weekList.getProductListsByType().keySet().stream())
                .collect(Collectors.toSet());
    }

    public WeekList getListForWeekNo(Integer weekNumber) {
        if (weekNumber <= 0) {
            throw new ValidationException("Requested weekNumber must be a positive value");
        }

        if (listsByWeek == null || listsByWeek.size() < weekNumber) {
            return null;
        }

        return listsByWeek.get(weekNumber - 1);
    }



}
