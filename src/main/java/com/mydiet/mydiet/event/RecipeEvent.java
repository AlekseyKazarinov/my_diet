package com.mydiet.mydiet.event;

import com.mydiet.mydiet.domain.entity.Recipe;

public class RecipeEvent extends DomainEvent{

    public RecipeEvent(Long id) {
        super(id);
        this.sourceEntityType = SourceEntity.RECIPE;
    }

}
