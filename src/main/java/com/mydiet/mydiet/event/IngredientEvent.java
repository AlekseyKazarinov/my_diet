package com.mydiet.mydiet.event;


public class IngredientEvent extends DomainEvent {

    public IngredientEvent(Long id) {
        super(id);
        this.sourceEntityType = SourceEntity.INGREDIENT;
    }
}
