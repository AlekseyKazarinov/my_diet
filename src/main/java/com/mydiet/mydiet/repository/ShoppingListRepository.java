package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

}
