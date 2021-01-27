package com.mydiet.mydiet.event;


import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class DomainEvent {

    protected Instant createdAt;

    protected final Long sourceId;
    protected SourceEntity sourceEntityType;
    protected Action action;

    protected Map<String,String> properties = new HashMap<>();

}
