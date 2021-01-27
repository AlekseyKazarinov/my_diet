package com.mydiet.mydiet.event.listener;

import com.mydiet.mydiet.event.DomainEvent;
import com.mydiet.mydiet.event.SourceEntity;
import com.mydiet.mydiet.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener extends DomainEventListener {

    private final ProductService productService;

    @Override
    boolean isAcceptable(DomainEvent event) {
        return SourceEntity.PRODUCT.equals(event.getSourceEntityType());
    }

    @Override
    void handle(DomainEvent event) {
        var productId = event.getSourceId();
        var product = productService.getProductOrThrow(productId);

        log.info("Ignore event: {}", event);

        // todo or ignore ?
    }
}
