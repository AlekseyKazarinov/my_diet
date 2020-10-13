package com.mydiet.mydiet.repository;


import com.mydiet.mydiet.domain.entity.Ingredient;
import org.springframework.data.repository.CrudRepository;

public interface IngredientRepository extends CrudRepository<Ingredient, Long> {

}
