package com.mydiet.mydiet.event;

import com.mydiet.mydiet.domain.entity.Ingredient;
import com.mydiet.mydiet.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DomainEventFactory {

    public DomainEvent createIngredientChangedEvent(Ingredient ingredient, Action action) {
        return DomainEvent.builder()
                .sourceId(ingredient.getId())
                .sourceEntityType(SourceEntity.INGREDIENT)
                .action(action)
                .createdAt(Instant.now())
                .build();
    }

    public DomainEvent createProductChangedEvent(Product product, Action action) {
        return DomainEvent.builder()
                .sourceId(product.getId())
                .sourceEntityType(SourceEntity.PRODUCT)
                .action(action)
                .createdAt(Instant.now())
                .build();
    }


}
