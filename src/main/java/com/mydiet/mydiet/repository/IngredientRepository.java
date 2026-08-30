package com.mydiet.mydiet.repository;


import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.Quantity;
import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;


public interface IngredientRepository extends CrudRepository<Ingredient, Long> {

    Optional<Ingredient> findByProductAndQuantity(Product product, Quantity quantity);
    @Transactional
    void deleteByIdIn(List<Long> ingredientIds);

}
