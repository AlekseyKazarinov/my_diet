package com.mydiet.mydiet.repository;


import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.QuantityUnit;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface IngredientRepository extends CrudRepository<Ingredient, Long> {

    Optional<Ingredient> findByProductAndTotalQuantityAndUnit(Product product, Double totalQuantity, QuantityUnit unit);

}
