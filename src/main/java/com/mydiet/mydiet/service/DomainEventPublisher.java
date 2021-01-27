package com.mydiet.mydiet.service;

import com.mydiet.mydiet.event.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DomainEventPublisher {

    private ApplicationEventPublisher applicationEventPublisher;

    //todo: implement async publishing
    public void publishEvent(Long sourceId, SourceEntity sourceEntity) {
        System.out.println("Publishing custom event. ");
        DomainEvent event = null;

        switch (sourceEntity) {
            case PRODUCT:
                event = new ProductEvent(sourceId);
                break;
            case INGREDIENT:
                event = new IngredientEvent(sourceId);
                break;
            case RECIPE:
                event = new RecipeEvent(sourceId);
                break;
            default:
                log.warn("SourceEntity {} can not be matched. Ignore creation event", sourceEntity);
        }

        if (event != null) {
            applicationEventPublisher.publishEvent(event);
            log.info("Event {} was published", event);
        }
    }


}
