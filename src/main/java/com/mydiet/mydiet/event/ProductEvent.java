package com.mydiet.mydiet.event;

public class ProductEvent extends DomainEvent {

    public ProductEvent(Long id) {
        super(id);
        this.sourceEntityType = SourceEntity.PRODUCT;
    }

}
