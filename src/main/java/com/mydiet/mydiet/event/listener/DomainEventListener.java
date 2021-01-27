package com.mydiet.mydiet.event.listener;

import com.mydiet.mydiet.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

@Slf4j
public abstract class DomainEventListener {

    @EventListener
    public void handleEvent(DomainEvent event) {
        try {
            if (isAcceptable(event)) {
                handle(event);
            }

        } catch (Exception e) {
            log.error("Failed to handle event {} with exception: {}", event, e);
        }
    }

    abstract boolean isAcceptable(DomainEvent event);

    abstract void handle(DomainEvent event);

}
