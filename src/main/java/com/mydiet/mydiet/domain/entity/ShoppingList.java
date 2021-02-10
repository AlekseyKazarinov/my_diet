package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
public class ShoppingList {

    @Id
    private Long nutritionProgramNumber;
    private List<WeekList> listsByWeek;

    @OneToOne
    @MapsId
    @JsonIgnore
    private NutritionProgram program;

    public Set<String> getAllProductNames() {
        return listsByWeek.stream()
                .flatMap(weekList -> Stream.of(weekList.getListsByProductType()))
                .flatMap(listsByProductType -> listsByProductType.values().stream())
                .flatMap(Collection::stream)
                .map(ProductRow::getProductName)
                .collect(Collectors.toSet());
    }

    public Set<ProductType> getAllProductTypes() {
        return listsByWeek.stream()
                .flatMap(weekList -> weekList.getListsByProductType().keySet().stream())
                .collect(Collectors.toSet());
    }



}
